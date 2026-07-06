package com.refactoredsolarpanels.menu;

import com.refactoredsolarpanels.block.SolarPanelBlockEntity;
import com.refactoredsolarpanels.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SolarPanelMenu extends AbstractContainerMenu {
    private static final int PANEL_SLOT_COUNT = 4;

    private final SolarPanelBlockEntity blockEntity;
    private final ContainerData data;

    public SolarPanelMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(containerId, playerInventory, getBlockEntity(playerInventory, buffer), new SimpleContainerData(7));
    }

    public SolarPanelMenu(int containerId, Inventory playerInventory, SolarPanelBlockEntity blockEntity) {
        this(containerId, playerInventory, blockEntity, blockEntity.getMenuData());
    }

    private SolarPanelMenu(int containerId, Inventory playerInventory, SolarPanelBlockEntity blockEntity, ContainerData data) {
        super(ModMenuTypes.SOLAR_PANEL.get(), containerId);
        this.blockEntity = blockEntity;
        this.data = data;
        ItemStackHandler handler = blockEntity == null ? new ItemStackHandler(PANEL_SLOT_COUNT) : blockEntity.getInventory();

        int chargeTier = blockEntity == null ? 0 : blockEntity.getPanelTier().getSourceTier();
        for (int slot = 0; slot < PANEL_SLOT_COUNT; slot++) {
            this.addSlot(new SlotItemHandler(handler, slot, 17 + slot * 18, 59) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return SolarPanelBlockEntity.canChargeItem(stack, chargeTier);
                }
            });
        }
        addPlayerInventory(playerInventory, 17, 86, 2, 58);
        this.addDataSlots(this.data);
    }

    public int getStoredEu() {
        return SyncedData.combine(this.data.get(0), this.data.get(1));
    }

    public int getCapacityEu() {
        return SyncedData.combine(this.data.get(2), this.data.get(3));
    }

    public int getProductionEuTick() {
        return this.data.get(4);
    }

    public int getGenerationState() {
        return this.data.get(5);
    }

    public int getMaxOutputEuTick() {
        return this.data.get(6);
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
            if (index < PANEL_SLOT_COUNT) {
                if (!this.moveItemStackTo(stack, PANEL_SLOT_COUNT, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, PANEL_SLOT_COUNT, false)) {
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

    private static SolarPanelBlockEntity getBlockEntity(Inventory inventory, FriendlyByteBuf buffer) {
        BlockEntity blockEntity = inventory.player.level().getBlockEntity(buffer.readBlockPos());
        return blockEntity instanceof SolarPanelBlockEntity solarPanel ? solarPanel : null;
    }
}
