package com.refactoredsolarpanels.compat.buildcraft;

import buildcraft.api.mj.IMjConnector;
import buildcraft.api.mj.IMjPassiveProvider;
import buildcraft.api.mj.IMjReadable;
import buildcraft.api.mj.IMjReceiver;
import buildcraft.api.mj.MjAPI;
import com.refactoredsolarpanels.config.AdvancedSolarCommonConfig;
import ic2.api.energy.prefab.BasicSink;
import ic2.api.energy.prefab.BasicSource;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class EnergyConverterBlockEntity extends BlockEntity {
    private static final String TAG_MJ_STORED = "MjStored";
    private static final double EU_PER_MJ = 2.5D;

    private final EnergyConverterType converterType;
    private final BasicSink euSink;
    private final BasicSource euSource;
    private final MjConnector mjConnector = new MjConnector();
    private LazyOptional<MjConnector> mjCapability = LazyOptional.of(() -> this.mjConnector);
    private long mjStored;
    private long throughputGameTime = Long.MIN_VALUE;
    private long mjReceivedThisTick;
    private long mjExtractedThisTick;

    public EnergyConverterBlockEntity(BlockPos pos, BlockState state) {
        super(BuildCraftConverters.ENERGY_CONVERTER.get(), pos, state);
        this.converterType = resolveType(state);

        if (this.converterType.isElectricEngine()) {
            this.euSink = new BasicSink(this, this.converterType.getEuCapacity(), this.converterType.getIc2Tier()) {
                @Override
                public boolean acceptsEnergyFrom(IEnergyEmitter emitter, Direction side) {
                    return EnergyConverterBlockEntity.this.isOperational()
                            && side == EnergyConverterBlockEntity.this.getFacing().getOpposite();
                }

                @Override
                public double getDemandedEnergy() {
                    return EnergyConverterBlockEntity.this.isOperational()
                            ? Math.min(EnergyConverterBlockEntity.this.converterType.getEuRate(), super.getDemandedEnergy())
                            : 0.0D;
                }
            };
            this.euSource = null;
        } else {
            this.euSink = null;
            this.euSource = new BasicSource(this, this.converterType.getEuCapacity(), this.converterType.getIc2Tier()) {
                @Override
                public boolean emitsEnergyTo(IEnergyAcceptor receiver, Direction side) {
                    return EnergyConverterBlockEntity.this.isOperational() && side == EnergyConverterBlockEntity.this.getFacing().getOpposite();
                }

                @Override
                public double getOfferedEnergy() {
                    return EnergyConverterBlockEntity.this.isOperational()
                            ? Math.min(EnergyConverterBlockEntity.this.converterType.getEuRate(), super.getOfferedEnergy())
                            : 0.0D;
                }
            };
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, EnergyConverterBlockEntity blockEntity) {
        blockEntity.tickServer(level);
    }

    public EnergyConverterType getConverterType() {
        return this.converterType;
    }

    public double getStoredEu() {
        return this.euSink != null ? this.euSink.getEnergyStored() : this.euSource.getEnergyStored();
    }

    public double getStoredMj() {
        return (double) this.mjStored / (double) MjAPI.MJ;
    }

    private void tickServer(Level level) {
        this.resetThroughputIfNeeded();
        if (this.euSink != null) {
            this.euSink.update();
        } else {
            this.euSource.update();
        }

        if (!this.isOperational()) {
            return;
        }

        boolean changed = this.converterType.isElectricEngine()
                ? this.convertEuToMj() | this.pushMjToFacingReceiver(level)
                : this.convertMjToEu();

        if (changed || level.getGameTime() % 20L == 0L) {
            this.setChanged();
        }
    }

    private boolean convertEuToMj() {
        long freeMj = this.getMjCapacityMicro() - this.mjStored;
        if (freeMj <= 0L || this.euSink.getEnergyStored() <= 0.0D) {
            return false;
        }

        long rateLimit = this.getMjRateMicro();
        long availableFromEu = (long) Math.floor(this.euSink.getEnergyStored() / EU_PER_MJ * MjAPI.MJ);
        long convertedMj = Math.min(rateLimit, Math.min(freeMj, availableFromEu));
        if (convertedMj <= 0L) {
            return false;
        }

        double euUsed = (double) convertedMj / (double) MjAPI.MJ * EU_PER_MJ;
        if (!this.euSink.useEnergy(euUsed)) {
            return false;
        }
        this.mjStored += convertedMj;
        return true;
    }

    private boolean convertMjToEu() {
        double freeEu = this.euSource.getFreeCapacity();
        if (freeEu <= 0.0D || this.mjStored <= 0L) {
            return false;
        }

        long usableForFreeEu = (long) Math.floor(freeEu / EU_PER_MJ * MjAPI.MJ);
        long consumedMj = Math.min(this.getMjRateMicro(), Math.min(this.mjStored, usableForFreeEu));
        if (consumedMj <= 0L) {
            return false;
        }

        double producedEu = (double) consumedMj / (double) MjAPI.MJ * EU_PER_MJ;
        double acceptedEu = this.euSource.addEnergy(producedEu);
        long acceptedMj = Math.min(consumedMj, Math.round(acceptedEu / EU_PER_MJ * MjAPI.MJ));
        this.mjStored = Math.max(0L, this.mjStored - acceptedMj);
        return acceptedMj > 0L;
    }

    private boolean pushMjToFacingReceiver(Level level) {
        this.resetThroughputIfNeeded();
        long available = Math.min(this.mjStored, this.getMjRateMicro() - this.mjExtractedThisTick);
        if (available <= 0L) {
            return false;
        }

        Direction facing = this.getFacing();
        BlockEntity target = level.getBlockEntity(this.worldPosition.relative(facing));
        if (target == null) {
            return false;
        }

        IMjReceiver receiver = target.getCapability(MjAPI.CAP_RECEIVER, facing.getOpposite()).orElse(null);
        if (receiver == null || !receiver.canConnect(this.mjConnector) || !this.mjConnector.canConnect(receiver)) {
            return false;
        }

        long offered = Math.min(available, Math.max(0L, receiver.getPowerRequested()));
        if (offered <= 0L) {
            return false;
        }

        long excess = Math.max(0L, receiver.receivePower(offered, false));
        long accepted = Math.max(0L, offered - Math.min(offered, excess));
        if (accepted <= 0L) {
            return false;
        }
        this.mjStored -= accepted;
        this.mjExtractedThisTick += accepted;
        return true;
    }

    boolean isOperational() {
        return AdvancedSolarCommonConfig.isBuildCraftConverterEnabled(this.converterType.getId())
                && (this.level == null || !this.level.hasNeighborSignal(this.worldPosition));
    }

    private Direction getFacing() {
        BlockState state = this.getBlockState();
        return state.hasProperty(EnergyConverterBlock.FACING) ? state.getValue(EnergyConverterBlock.FACING) : Direction.NORTH;
    }

    private long getMjRateMicro() {
        return Math.round(this.converterType.getMjRate() * MjAPI.MJ);
    }

    private long getMjCapacityMicro() {
        return Math.round(this.converterType.getMjCapacity() * MjAPI.MJ);
    }

    private void resetThroughputIfNeeded() {
        if (this.level == null) {
            return;
        }
        long gameTime = this.level.getGameTime();
        if (this.throughputGameTime != gameTime) {
            this.throughputGameTime = gameTime;
            this.mjReceivedThisTick = 0L;
            this.mjExtractedThisTick = 0L;
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.level != null && !this.level.isClientSide) {
            if (this.euSink != null) {
                this.euSink.onLoad();
            } else {
                this.euSource.onLoad();
            }
        }
    }

    @Override
    public void setRemoved() {
        if (this.level != null && !this.level.isClientSide) {
            if (this.euSink != null) {
                this.euSink.invalidate();
            } else {
                this.euSource.invalidate();
            }
        }
        super.setRemoved();
    }

    @Override
    public void onChunkUnloaded() {
        if (this.level != null && !this.level.isClientSide) {
            if (this.euSink != null) {
                this.euSink.onChunkUnload();
            } else {
                this.euSource.onChunkUnload();
            }
        }
        super.onChunkUnloaded();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (this.euSink != null) {
            this.euSink.readFromNBT(tag);
        } else {
            this.euSource.readFromNBT(tag);
        }
        this.mjStored = Math.max(0L, Math.min(tag.getLong(TAG_MJ_STORED), this.getMjCapacityMicro()));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.euSink != null) {
            this.euSink.writeToNBT(tag);
        } else {
            this.euSource.writeToNBT(tag);
        }
        tag.putLong(TAG_MJ_STORED, this.mjStored);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.mjCapability.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        this.mjCapability = LazyOptional.of(() -> this.mjConnector);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        boolean mjSide = side == null || side == this.getFacing();
        if (mjSide && capability == MjAPI.CAP_CONNECTOR) {
            return this.mjCapability.cast();
        }
        if (mjSide && capability == MjAPI.CAP_READABLE) {
            return this.mjCapability.cast();
        }
        if (mjSide && this.converterType.isElectricEngine() && capability == MjAPI.CAP_PASSIVE_PROVIDER) {
            return this.mjCapability.cast();
        }
        if (mjSide && !this.converterType.isElectricEngine() && capability == MjAPI.CAP_RECEIVER) {
            return this.mjCapability.cast();
        }
        return super.getCapability(capability, side);
    }

    private static EnergyConverterType resolveType(BlockState state) {
        if (state.getBlock() instanceof EnergyConverterBlock block) {
            return block.getConverterType();
        }
        return EnergyConverterType.LV_ELECTRIC_ENGINE;
    }

    private final class MjConnector implements IMjPassiveProvider, IMjReceiver, IMjReadable {
        @Override
        public boolean canConnect(IMjConnector other) {
            return EnergyConverterBlockEntity.this.isOperational() && other != this;
        }

        @Override
        public long getPowerRequested() {
            if (EnergyConverterBlockEntity.this.converterType.isElectricEngine() || !EnergyConverterBlockEntity.this.isOperational()) {
                return 0L;
            }
            EnergyConverterBlockEntity.this.resetThroughputIfNeeded();
            long throughputRemaining = EnergyConverterBlockEntity.this.getMjRateMicro() - EnergyConverterBlockEntity.this.mjReceivedThisTick;
            long capacityRemaining = EnergyConverterBlockEntity.this.getMjCapacityMicro() - EnergyConverterBlockEntity.this.mjStored;
            return Math.max(0L, Math.min(throughputRemaining, capacityRemaining));
        }

        @Override
        public long receivePower(long power, boolean simulate) {
            long accepted = Math.min(Math.max(0L, power), this.getPowerRequested());
            if (!simulate && accepted > 0L) {
                EnergyConverterBlockEntity.this.mjStored += accepted;
                EnergyConverterBlockEntity.this.mjReceivedThisTick += accepted;
                EnergyConverterBlockEntity.this.setChanged();
            }
            return power - accepted;
        }

        @Override
        public long extractPower(long min, long max, boolean simulate) {
            if (!EnergyConverterBlockEntity.this.converterType.isElectricEngine() || !EnergyConverterBlockEntity.this.isOperational()) {
                return 0L;
            }
            EnergyConverterBlockEntity.this.resetThroughputIfNeeded();
            long throughputRemaining = EnergyConverterBlockEntity.this.getMjRateMicro() - EnergyConverterBlockEntity.this.mjExtractedThisTick;
            long extracted = Math.min(Math.max(0L, max), Math.min(EnergyConverterBlockEntity.this.mjStored, throughputRemaining));
            if (extracted < Math.max(0L, min)) {
                return 0L;
            }
            if (!simulate && extracted > 0L) {
                EnergyConverterBlockEntity.this.mjStored -= extracted;
                EnergyConverterBlockEntity.this.mjExtractedThisTick += extracted;
                EnergyConverterBlockEntity.this.setChanged();
            }
            return extracted;
        }

        @Override
        public long getStored() {
            return EnergyConverterBlockEntity.this.mjStored;
        }

        @Override
        public long getCapacity() {
            return EnergyConverterBlockEntity.this.getMjCapacityMicro();
        }
    }
}
