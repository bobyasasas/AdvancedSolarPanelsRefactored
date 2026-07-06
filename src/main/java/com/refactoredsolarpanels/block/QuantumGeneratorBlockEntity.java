package com.refactoredsolarpanels.block;

import com.refactoredsolarpanels.menu.QuantumGeneratorMenu;
import com.refactoredsolarpanels.registry.ModBlockEntities;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IMultiEnergySource;
import ic2.api.info.ILocatable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class QuantumGeneratorBlockEntity extends BlockEntity implements IMultiEnergySource, ILocatable, MenuProvider {
    private static final String TAG_PRODUCTION = "Production";
    private static final String TAG_TIER = "Tier";

    private final ContainerData menuData = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> com.refactoredsolarpanels.menu.SyncedData.low(QuantumGeneratorBlockEntity.this.production);
                case 1 -> com.refactoredsolarpanels.menu.SyncedData.high(QuantumGeneratorBlockEntity.this.production);
                case 2 -> QuantumGeneratorBlockEntity.this.tier;
                case 3 -> QuantumGeneratorBlockEntity.this.active ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return 4;
        }
    };
    private int production = 512;
    private int tier = 3;
    private boolean active;
    private boolean addedToEnergyNet;

    public QuantumGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.QUANTUM_GENERATOR.get(), pos, state);
        this.active = state.hasProperty(QuantumGeneratorBlock.ACTIVE) && state.getValue(QuantumGeneratorBlock.ACTIVE);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, QuantumGeneratorBlockEntity blockEntity) {
        blockEntity.tickServer(level, pos, state);
    }

    private void tickServer(Level level, BlockPos pos, BlockState state) {
        if (!this.addedToEnergyNet) {
            this.addToEnergyNet();
        }

        boolean shouldBeActive = !level.hasNeighborSignal(pos);
        if (this.active != shouldBeActive) {
            this.active = shouldBeActive;
            if (state.hasProperty(QuantumGeneratorBlock.ACTIVE)) {
                level.setBlock(pos, state.setValue(QuantumGeneratorBlock.ACTIVE, shouldBeActive), 3);
            }
            this.setChanged();
        }
    }

    public ContainerData getMenuData() {
        return this.menuData;
    }

    public int getProduction() {
        return this.production;
    }

    public int getGeneratorTier() {
        return this.tier;
    }

    public boolean isActive() {
        return this.active;
    }

    public void handleGuiEvent(int event) {
        int previousProduction = this.production;
        int previousTier = this.tier;
        switch (event / 10) {
            case 0 -> this.changeProduction(switch (event % 10) {
                case 0 -> -100;
                case 1 -> -10;
                case 2 -> -1;
                case 3 -> 1;
                case 4 -> 10;
                case 5 -> 100;
                default -> 0;
            });
            case 1 -> this.changeProduction(switch (event % 10) {
                case 0 -> -500;
                case 1 -> -50;
                case 2 -> -5;
                case 3 -> 5;
                case 4 -> 50;
                case 5 -> 500;
                default -> 0;
            });
            case 2 -> this.tier = Math.max(1, event % 10 + 1);
            default -> {
            }
        }
        if (this.production != previousProduction || this.tier != previousTier) {
            this.setChanged();
            if (this.level != null && !this.level.isClientSide) {
                this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
            }
        }
    }

    private void changeProduction(int value) {
        this.production = Math.max(0, this.production + value);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.addToEnergyNet();
    }

    @Override
    public void setRemoved() {
        this.removeFromEnergyNet();
        super.setRemoved();
    }

    @Override
    public void onChunkUnloaded() {
        this.removeFromEnergyNet();
        super.onChunkUnloaded();
    }

    private void addToEnergyNet() {
        if (this.level != null && !this.level.isClientSide && !this.addedToEnergyNet && EnergyNet.instance != null) {
            EnergyNet.instance.addLocatableTile(this);
            this.addedToEnergyNet = true;
        }
    }

    private void removeFromEnergyNet() {
        if (this.level != null && !this.level.isClientSide && this.addedToEnergyNet && EnergyNet.instance != null) {
            EnergyNet.instance.removeTile(this);
            this.addedToEnergyNet = false;
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(TAG_PRODUCTION)) {
            this.production = tag.getInt(TAG_PRODUCTION);
        }
        if (tag.contains(TAG_TIER)) {
            this.tier = Math.max(1, tag.getInt(TAG_TIER));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt(TAG_PRODUCTION, this.production);
        tag.putInt(TAG_TIER, this.tier);
    }

    @Override
    public boolean emitsEnergyTo(IEnergyAcceptor receiver, Direction side) {
        return true;
    }

    @Override
    public double getOfferedEnergy() {
        if (!this.active || this.production <= 0) {
            return 0.0D;
        }
        return this.sendMultipleEnergyPackets() ? (double) this.production / (double) this.getMultipleEnergyPacketAmount() : this.production;
    }

    @Override
    public void drawEnergy(double amount) {
    }

    @Override
    public int getSourceTier() {
        return this.tier;
    }

    @Override
    public boolean sendMultipleEnergyPackets() {
        return this.production > getTierPower();
    }

    @Override
    public int getMultipleEnergyPacketAmount() {
        if (this.production <= 0) {
            return 1;
        }
        return Math.max(1, (int) Math.ceil((double) this.production / getTierPower()));
    }

    private double getTierPower() {
        return EnergyNet.instance == null ? 8.0D * Math.pow(4.0D, this.tier) : EnergyNet.instance.getPowerFromTier(this.tier);
    }

    @Override
    public BlockPos getPosition() {
        return this.worldPosition;
    }

    @Override
    public Level getWorldObj() {
        return this.level;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.advanced_solar_panels_refactored.quantum_generator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new QuantumGeneratorMenu(containerId, playerInventory, this);
    }
}
