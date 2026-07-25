package com.refactoredsolarpanels.block;

import com.refactoredsolarpanels.menu.MolecularTransformerMenu;
import com.refactoredsolarpanels.recipe.MolecularTransformerRecipe;
import com.refactoredsolarpanels.registry.ModBlockEntities;
import com.refactoredsolarpanels.registry.ModRecipeTypes;
import ic2.api.energy.prefab.BasicSink;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MolecularTransformerBlockEntity extends BlockEntity implements MenuProvider {
    private static final int ACTIVE_STATE_CHECK_INTERVAL = 40;
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final String TAG_INVENTORY = "Inventory";
    private static final String TAG_ENERGY_USED = "EnergyUsed";
    private static final String TAG_RECIPE_ENERGY = "RecipeEnergy";
    private static final String TAG_RECIPE_INPUT = "RecipeInput";
    private static final String TAG_RECIPE_OUTPUT = "RecipeOutput";
    private static final String TAG_LAST_ENERGY = "LastEnergyInput";

    private final BasicSink energySink = new BasicSink(this, 1000000.0D, 14);
    private final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            MolecularTransformerBlockEntity.this.setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot == INPUT_SLOT && MolecularTransformerBlockEntity.this.isRecipeInput(stack);
        }
    };
    private final LazyOptional<IItemHandler> automationCapability = LazyOptional.of(() -> new IItemHandler() {
        @Override
        public int getSlots() {
            return MolecularTransformerBlockEntity.this.inventory.getSlots();
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return MolecularTransformerBlockEntity.this.inventory.getStackInSlot(slot);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (slot != INPUT_SLOT) {
                return stack;
            }
            return MolecularTransformerBlockEntity.this.inventory.insertItem(slot, stack, simulate);
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot != OUTPUT_SLOT) {
                return ItemStack.EMPTY;
            }
            return MolecularTransformerBlockEntity.this.inventory.extractItem(slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return MolecularTransformerBlockEntity.this.inventory.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot == INPUT_SLOT && MolecularTransformerBlockEntity.this.inventory.isItemValid(slot, stack);
        }
    });
    private final ContainerData menuData = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> com.refactoredsolarpanels.menu.SyncedData.low(MolecularTransformerBlockEntity.this.energyUsed);
                case 1 -> com.refactoredsolarpanels.menu.SyncedData.high(MolecularTransformerBlockEntity.this.energyUsed);
                case 2 -> com.refactoredsolarpanels.menu.SyncedData.low(MolecularTransformerBlockEntity.this.recipeEnergy);
                case 3 -> com.refactoredsolarpanels.menu.SyncedData.high(MolecularTransformerBlockEntity.this.recipeEnergy);
                case 4 -> com.refactoredsolarpanels.menu.SyncedData.low((int) Math.round(MolecularTransformerBlockEntity.this.lastEnergyInput));
                case 5 -> com.refactoredsolarpanels.menu.SyncedData.high((int) Math.round(MolecularTransformerBlockEntity.this.lastEnergyInput));
                case 6 -> MolecularTransformerBlockEntity.this.active ? 1 : 0;
                case 7 -> com.refactoredsolarpanels.menu.SyncedData.low((int) Math.round(MolecularTransformerBlockEntity.this.energySink.getEnergyStored()));
                case 8 -> com.refactoredsolarpanels.menu.SyncedData.high((int) Math.round(MolecularTransformerBlockEntity.this.energySink.getEnergyStored()));
                case 9 -> com.refactoredsolarpanels.menu.SyncedData.low((int) Math.round(MolecularTransformerBlockEntity.this.energySink.getCapacity()));
                case 10 -> com.refactoredsolarpanels.menu.SyncedData.high((int) Math.round(MolecularTransformerBlockEntity.this.energySink.getCapacity()));
                case 11 -> com.refactoredsolarpanels.menu.SyncedData.low(BuiltInRegistries.ITEM.getId(MolecularTransformerBlockEntity.this.pendingInput.getItem()));
                case 12 -> com.refactoredsolarpanels.menu.SyncedData.high(BuiltInRegistries.ITEM.getId(MolecularTransformerBlockEntity.this.pendingInput.getItem()));
                case 13 -> MolecularTransformerBlockEntity.this.pendingInput.getCount();
                case 14 -> com.refactoredsolarpanels.menu.SyncedData.low(BuiltInRegistries.ITEM.getId(MolecularTransformerBlockEntity.this.pendingOutput.getItem()));
                case 15 -> com.refactoredsolarpanels.menu.SyncedData.high(BuiltInRegistries.ITEM.getId(MolecularTransformerBlockEntity.this.pendingOutput.getItem()));
                case 16 -> MolecularTransformerBlockEntity.this.pendingOutput.getCount();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
                return 17;
        }
    };

    private ItemStack pendingInput = ItemStack.EMPTY;
    private ItemStack pendingOutput = ItemStack.EMPTY;
    private int energyUsed;
    private int recipeEnergy;
    private double lastEnergyInput;
    private boolean active;

    public MolecularTransformerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MOLECULAR_TRANSFORMER.get(), pos, state);
        this.active = state.hasProperty(MolecularTransformerBlock.ACTIVE) && state.getValue(MolecularTransformerBlock.ACTIVE);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MolecularTransformerBlockEntity blockEntity) {
        blockEntity.tickServer(level, pos, state);
    }

    private void tickServer(Level level, BlockPos pos, BlockState state) {
        this.energySink.update();

        if (this.recipeEnergy <= 0 && hasStoredEnergy()) {
            this.tryStartRecipe(level);
        }

        this.lastEnergyInput = 0.0D;
        if (canProcessRecipe()) {
            double stored = this.energySink.getEnergyStored();
            if (stored > 0.0D) {
                double used = Math.min(stored, this.recipeEnergy - this.energyUsed);
                if (this.energySink.useEnergy(used)) {
                    this.energyUsed += (int) Math.round(used);
                    this.lastEnergyInput = used;
                }
            }

            if (this.energyUsed >= this.recipeEnergy) {
                insertOutput(this.pendingOutput.copy());
                this.pendingInput = ItemStack.EMPTY;
                this.pendingOutput = ItemStack.EMPTY;
                this.recipeEnergy = 0;
                this.energyUsed = 0;
            }
        }

        if (shouldCheckActiveState(level, pos)) {
            this.setActiveState(level, pos, state, shouldBeActive());
        }

        if (level.getGameTime() % 20L == 0L) {
            this.setChanged();
        }
    }

    private boolean hasStoredEnergy() {
        return this.energySink.getEnergyStored() > 0.0D;
    }

    private boolean canProcessRecipe() {
        return this.recipeEnergy > 0 && canInsertOutput(this.pendingOutput);
    }

    private boolean shouldBeActive() {
        return this.energyUsed > 0 && this.lastEnergyInput > 0.0D;
    }

    private static boolean shouldCheckActiveState(Level level, BlockPos pos) {
        return Math.floorMod(level.getGameTime() + pos.asLong(), ACTIVE_STATE_CHECK_INTERVAL) == 0L;
    }

    private void setActiveState(Level level, BlockPos pos, BlockState state, boolean nextActive) {
        this.active = nextActive;
        if (state.hasProperty(MolecularTransformerBlock.ACTIVE) && state.getValue(MolecularTransformerBlock.ACTIVE) != nextActive) {
            level.setBlock(pos, state.setValue(MolecularTransformerBlock.ACTIVE, nextActive), 3);
        }
    }

    private void tryStartRecipe(Level level) {
        ItemStack input = this.inventory.getStackInSlot(INPUT_SLOT);
        if (input.isEmpty()) {
            return;
        }

        Container container = new SimpleContainer(input);
        level.getRecipeManager().getRecipeFor(ModRecipeTypes.MOLECULAR_TRANSFORMING.get(), container, level).ifPresent(recipe -> {
            if (!canInsertOutput(recipe.getResult())) {
                return;
            }
            this.pendingInput = input.copyWithCount(recipe.getInputCount());
            this.pendingOutput = recipe.getResult();
            this.recipeEnergy = recipe.getEnergy();
            this.energyUsed = 0;
            this.inventory.extractItem(INPUT_SLOT, recipe.getInputCount(), false);
            this.setChanged();
        });
    }

    private boolean isRecipeInput(ItemStack stack) {
        if (stack.isEmpty() || this.level == null) {
            return false;
        }
        return this.level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.MOLECULAR_TRANSFORMING.get()).stream()
                .anyMatch(recipe -> recipe.getIngredient().test(stack));
    }

    private boolean canInsertOutput(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        ItemStack existing = this.inventory.getStackInSlot(OUTPUT_SLOT);
        if (existing.isEmpty()) {
            return true;
        }
        return ItemStack.isSameItemSameTags(existing, stack) && existing.getCount() + stack.getCount() <= existing.getMaxStackSize();
    }

    private void insertOutput(ItemStack stack) {
        ItemStack existing = this.inventory.getStackInSlot(OUTPUT_SLOT);
        if (existing.isEmpty()) {
            this.inventory.setStackInSlot(OUTPUT_SLOT, stack);
        } else {
            existing.grow(stack.getCount());
            this.inventory.setStackInSlot(OUTPUT_SLOT, existing);
        }
    }

    public ItemStackHandler getInventory() {
        return this.inventory;
    }

    public ContainerData getMenuData() {
        return this.menuData;
    }

    public int getEnergyUsed() {
        return this.energyUsed;
    }

    public int getRecipeEnergy() {
        return this.recipeEnergy;
    }

    public double getLastEnergyInput() {
        return this.lastEnergyInput;
    }

    public double getStoredEu() {
        return this.energySink.getEnergyStored();
    }

    public double getCapacityEu() {
        return this.energySink.getCapacity();
    }

    public boolean isActive() {
        return this.active;
    }

    public ItemStack getPendingOutput() {
        return this.pendingOutput.copy();
    }

    public ItemStack getPendingInput() {
        return this.pendingInput.copy();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.level != null && !this.level.isClientSide) {
            this.energySink.onLoad();
        }
    }

    @Override
    public void setRemoved() {
        if (this.level != null && !this.level.isClientSide) {
            this.energySink.invalidate();
        }
        this.automationCapability.invalidate();
        super.setRemoved();
    }

    @Override
    public void onChunkUnloaded() {
        if (this.level != null && !this.level.isClientSide) {
            this.energySink.onChunkUnload();
        }
        super.onChunkUnloaded();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.energySink.readFromNBT(tag);
        if (tag.contains(TAG_INVENTORY)) {
            this.inventory.deserializeNBT(tag.getCompound(TAG_INVENTORY));
        }
        this.energyUsed = tag.getInt(TAG_ENERGY_USED);
        this.recipeEnergy = tag.getInt(TAG_RECIPE_ENERGY);
        this.lastEnergyInput = tag.getDouble(TAG_LAST_ENERGY);
        this.pendingInput = tag.contains(TAG_RECIPE_INPUT) ? ItemStack.of(tag.getCompound(TAG_RECIPE_INPUT)) : ItemStack.EMPTY;
        this.pendingOutput = tag.contains(TAG_RECIPE_OUTPUT) ? ItemStack.of(tag.getCompound(TAG_RECIPE_OUTPUT)) : ItemStack.EMPTY;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        this.energySink.writeToNBT(tag);
        tag.put(TAG_INVENTORY, this.inventory.serializeNBT());
        tag.putInt(TAG_ENERGY_USED, this.energyUsed);
        tag.putInt(TAG_RECIPE_ENERGY, this.recipeEnergy);
        tag.putDouble(TAG_LAST_ENERGY, this.lastEnergyInput);
        if (!this.pendingInput.isEmpty()) {
            tag.put(TAG_RECIPE_INPUT, this.pendingInput.save(new CompoundTag()));
        }
        if (!this.pendingOutput.isEmpty()) {
            tag.put(TAG_RECIPE_OUTPUT, this.pendingOutput.save(new CompoundTag()));
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.advanced_solar_panels_refactored.molecular_transformer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new MolecularTransformerMenu(containerId, playerInventory, this);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable net.minecraft.core.Direction side) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return this.automationCapability.cast();
        }
        return super.getCapability(capability, side);
    }
}
