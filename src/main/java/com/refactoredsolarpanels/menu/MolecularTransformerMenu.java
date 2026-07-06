package com.refactoredsolarpanels.menu;

import com.refactoredsolarpanels.block.MolecularTransformerBlockEntity;
import com.refactoredsolarpanels.registry.ModMenuTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class MolecularTransformerMenu extends AbstractContainerMenu {
    private static final int MACHINE_SLOT_COUNT = 2;

    private final MolecularTransformerBlockEntity blockEntity;
    private final ContainerData data;

    public MolecularTransformerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(containerId, playerInventory, getBlockEntity(playerInventory, buffer), new SimpleContainerData(17));
    }

    public MolecularTransformerMenu(int containerId, Inventory playerInventory, MolecularTransformerBlockEntity blockEntity) {
        this(containerId, playerInventory, blockEntity, blockEntity.getMenuData());
    }

    private MolecularTransformerMenu(int containerId, Inventory playerInventory, MolecularTransformerBlockEntity blockEntity, ContainerData data) {
        super(ModMenuTypes.MOLECULAR_TRANSFORMER.get(), containerId);
        this.blockEntity = blockEntity;
        this.data = data;
        ItemStackHandler handler = blockEntity == null ? new ItemStackHandler(MACHINE_SLOT_COUNT) : blockEntity.getInventory();

        this.addSlot(new SlotItemHandler(handler, 0, 20, 27));
        this.addSlot(new SlotItemHandler(handler, 1, 20, 68) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        addPlayerInventory(playerInventory, 18, 98, 5, 67);
        this.addDataSlots(this.data);
    }

    public int getEnergyUsed() {
        return SyncedData.combine(this.data.get(0), this.data.get(1));
    }

    public int getRecipeEnergy() {
        return SyncedData.combine(this.data.get(2), this.data.get(3));
    }

    public int getLastEnergyInput() {
        return SyncedData.combine(this.data.get(4), this.data.get(5));
    }

    public boolean isActive() {
        return this.data.get(6) != 0;
    }

    public int getStoredEu() {
        return SyncedData.combine(this.data.get(7), this.data.get(8));
    }

    public int getCapacityEu() {
        return SyncedData.combine(this.data.get(9), this.data.get(10));
    }

    public ItemStack getInputStack() {
        return this.slots.get(0).getItem();
    }

    public ItemStack getOutputStack() {
        return this.slots.get(1).getItem();
    }

    public ItemStack getPendingInputStack() {
        return stackFromData(11, 12, 13);
    }

    public ItemStack getPendingOutputStack() {
        return stackFromData(14, 15, 16);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.blockEntity != null && player.distanceToSqr(
                this.blockEntity.getBlockPos().getX() + 0.5D,
                this.blockEntity.getBlockPos().getY() + 0.5D,
                this.blockEntity.getBlockPos().getZ() + 0.5D
        ) <= 64.0D;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        var slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index < MACHINE_SLOT_COUNT) {
                if (!this.moveItemStackTo(stack, MACHINE_SLOT_COUNT, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }

    private void addPlayerInventory(Inventory inventory, int x, int y, int spacing, int hotbarOffset) {
        int step = 16 + spacing;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new net.minecraft.world.inventory.Slot(inventory, column + row * 9 + 9, x + column * step, y + row * step));
            }
        }
        for (int column = 0; column < 9; column++) {
            this.addSlot(new net.minecraft.world.inventory.Slot(inventory, column, x + column * step, y + hotbarOffset));
        }
    }

    private ItemStack stackFromData(int lowIndex, int highIndex, int countIndex) {
        int count = this.data.get(countIndex);
        if (count <= 0) {
            return ItemStack.EMPTY;
        }
        Item item = BuiltInRegistries.ITEM.byId(SyncedData.combine(this.data.get(lowIndex), this.data.get(highIndex)));
        return item == Items.AIR ? ItemStack.EMPTY : new ItemStack(item, count);
    }

    private static MolecularTransformerBlockEntity getBlockEntity(Inventory inventory, FriendlyByteBuf buffer) {
        BlockEntity blockEntity = inventory.player.level().getBlockEntity(buffer.readBlockPos());
        return blockEntity instanceof MolecularTransformerBlockEntity molecularTransformer ? molecularTransformer : null;
    }
}
