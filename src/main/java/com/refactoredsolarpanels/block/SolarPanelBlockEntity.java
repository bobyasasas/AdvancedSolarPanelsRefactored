package com.refactoredsolarpanels.block;

import com.refactoredsolarpanels.registry.ModBlockEntities;
import com.refactoredsolarpanels.menu.SolarPanelMenu;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.prefab.BasicSource;
import ic2.api.item.ElectricItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SolarPanelBlockEntity extends BlockEntity implements MenuProvider {
    private static final int SUN_CHECK_INTERVAL = 128;
    private static final String TAG_SKY_LIGHT = "SkyLight";
    private static final String TAG_GENERATING = "Generating";
    private static final String TAG_LAST_GENERATION = "LastGeneration";
    private static final String TAG_INVENTORY = "Inventory";

    private final SolarPanelTier tier;
    private final BasicSource energySource;
    private final ItemStackHandler inventory = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            SolarPanelBlockEntity.this.setChanged();
        }
    };
    private final LazyOptional<ItemStackHandler> inventoryCapability = LazyOptional.of(() -> this.inventory);
    private final ContainerData menuData = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> com.refactoredsolarpanels.menu.SyncedData.low((int) Math.round(SolarPanelBlockEntity.this.getStoredEu()));
                case 1 -> com.refactoredsolarpanels.menu.SyncedData.high((int) Math.round(SolarPanelBlockEntity.this.getStoredEu()));
                case 2 -> com.refactoredsolarpanels.menu.SyncedData.low((int) Math.round(SolarPanelBlockEntity.this.getCapacityEu()));
                case 3 -> com.refactoredsolarpanels.menu.SyncedData.high((int) Math.round(SolarPanelBlockEntity.this.getCapacityEu()));
                case 4 -> (int) Math.round(SolarPanelBlockEntity.this.getCurrentProductionEuTick());
                case 5 -> SolarPanelBlockEntity.this.generationState.ordinal();
                case 6 -> SolarPanelBlockEntity.this.getMaxOutputEuTick();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return 7;
        }
    };
    private int ticker;
    private float skyLight;
    private boolean generating;
    private double lastGenerationEuTick;
    private GenerationState generationState = GenerationState.NONE;

    public SolarPanelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOLAR_PANEL.get(), pos, state);
        this.tier = resolveTier(state);
        this.energySource = new BasicSource(this, this.tier.getCapacityEu(), this.tier.getSourceTier());
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SolarPanelBlockEntity blockEntity) {
        blockEntity.tickServer(level);
    }

    public SolarPanelTier getPanelTier() {
        return this.tier;
    }

    public double getStoredEu() {
        return this.energySource.getEnergyStored();
    }

    public double getCapacityEu() {
        return this.energySource.getCapacity();
    }

    public double getCurrentProductionEuTick() {
        return this.lastGenerationEuTick;
    }

    public int getMaxOutputEuTick() {
        double maxOutput = EnergyNet.instance == null ? 8.0D * Math.pow(4.0D, this.tier.getSourceTier()) : EnergyNet.instance.getPowerFromTier(this.tier.getSourceTier());
        return (int) Math.round(maxOutput);
    }

    public boolean isGenerating() {
        return this.generating;
    }

    public ItemStackHandler getInventory() {
        return this.inventory;
    }

    public ContainerData getMenuData() {
        return this.menuData;
    }

    public GenerationState getGenerationState() {
        return this.generationState;
    }

    private void tickServer(Level level) {
        this.energySource.update();

        if (this.ticker == 0 || this.ticker % SUN_CHECK_INTERVAL == 0) {
            this.updateSunVisibility(level);
        }
        this.ticker++;

        double production = switch (this.generationState) {
            case DAY -> this.tier.getDayProductionEuTick();
            case NIGHT -> this.tier.getNightProductionEuTick();
            case NONE -> 0.0D;
        };

        if (production > 0.0D && this.energySource.getFreeCapacity() > 0.0D) {
            double accepted = this.energySource.addEnergy(production);
            this.lastGenerationEuTick = accepted;
            this.generating = accepted > 0.0D;
        } else {
            this.lastGenerationEuTick = 0.0D;
            this.generating = false;
        }

        for (int slot = 0; slot < this.inventory.getSlots(); slot++) {
            ItemStack stack = this.inventory.getStackInSlot(slot);
            if (this.energySource.charge(stack)) {
                this.inventory.setStackInSlot(slot, stack);
            }
        }

        if (this.ticker % 20 == 0) {
            this.setChanged();
        }
    }

    private void updateSunVisibility(Level level) {
        if (!level.dimensionType().hasSkyLight()) {
            this.skyLight = 0.0F;
            this.generationState = GenerationState.NONE;
            return;
        }

        BlockPos skyPos = this.worldPosition.above();
        if (!level.canSeeSky(skyPos)) {
            this.skyLight = 0.0F;
            this.generationState = GenerationState.NONE;
            return;
        }

        float sunBrightness = Mth.clamp((float) Math.cos(level.getSunAngle(1.0F)) * 2.0F + 0.2F, 0.0F, 1.0F);
        boolean daylight = level.isDay() && !level.isRainingAt(this.worldPosition) && sunBrightness > 0.0F;
        this.skyLight = Mth.clamp(level.getBrightness(LightLayer.SKY, skyPos) / 15.0F * sunBrightness, 0.0F, 1.0F);
        this.generationState = daylight ? GenerationState.DAY : GenerationState.NIGHT;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.level != null && !this.level.isClientSide) {
            this.energySource.onLoad();
        }
    }

    @Override
    public void setRemoved() {
        if (this.level != null && !this.level.isClientSide) {
            this.energySource.invalidate();
        }
        this.inventoryCapability.invalidate();
        super.setRemoved();
    }

    @Override
    public void onChunkUnloaded() {
        if (this.level != null && !this.level.isClientSide) {
            this.energySource.onChunkUnload();
        }
        super.onChunkUnloaded();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.energySource.readFromNBT(tag);
        if (tag.contains(TAG_INVENTORY)) {
            this.inventory.deserializeNBT(tag.getCompound(TAG_INVENTORY));
        }
        this.skyLight = tag.getFloat(TAG_SKY_LIGHT);
        this.generating = tag.getBoolean(TAG_GENERATING);
        this.lastGenerationEuTick = tag.getDouble(TAG_LAST_GENERATION);
        this.generationState = GenerationState.byId(tag.getInt("GenerationState"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        this.energySource.writeToNBT(tag);
        tag.put(TAG_INVENTORY, this.inventory.serializeNBT());
        tag.putFloat(TAG_SKY_LIGHT, this.skyLight);
        tag.putBoolean(TAG_GENERATING, this.generating);
        tag.putDouble(TAG_LAST_GENERATION, this.lastGenerationEuTick);
        tag.putInt("GenerationState", this.generationState.ordinal());
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.advanced_solar_panels_refactored." + this.tier.getId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new SolarPanelMenu(containerId, playerInventory, this);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable net.minecraft.core.Direction side) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return this.inventoryCapability.cast();
        }
        return super.getCapability(capability, side);
    }

    private static SolarPanelTier resolveTier(BlockState state) {
        if (state.getBlock() instanceof SolarPanelBlock block) {
            return block.getTier();
        }
        return SolarPanelTier.ADVANCED;
    }

    public static boolean canChargeItem(ItemStack stack, int tier) {
        return !stack.isEmpty()
                && ElectricItem.manager != null
                && ElectricItem.manager.charge(stack, Double.POSITIVE_INFINITY, tier, false, true) > 0.0D;
    }

    public enum GenerationState {
        NONE,
        NIGHT,
        DAY;

        static GenerationState byId(int id) {
            if (id < 0 || id >= values().length) {
                return NONE;
            }
            return values()[id];
        }
    }
}
