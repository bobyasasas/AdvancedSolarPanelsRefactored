package com.refactoredsolarpanels.menu;

import com.refactoredsolarpanels.block.QuantumGeneratorBlockEntity;
import com.refactoredsolarpanels.registry.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class QuantumGeneratorMenu extends AbstractContainerMenu {
    private final QuantumGeneratorBlockEntity blockEntity;
    private final ContainerData data;
    private final BlockPos blockPos;

    public QuantumGeneratorMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(containerId, playerInventory, getMenuContext(playerInventory, buffer), new SimpleContainerData(4));
    }

    public QuantumGeneratorMenu(int containerId, Inventory playerInventory, QuantumGeneratorBlockEntity blockEntity) {
        this(containerId, playerInventory, blockEntity, blockEntity.getBlockPos(), blockEntity.getMenuData());
    }

    private QuantumGeneratorMenu(int containerId, Inventory playerInventory, MenuContext context, ContainerData data) {
        this(containerId, playerInventory, context.blockEntity(), context.blockPos(), data);
    }

    private QuantumGeneratorMenu(int containerId, Inventory playerInventory, QuantumGeneratorBlockEntity blockEntity, BlockPos blockPos, ContainerData data) {
        super(ModMenuTypes.QUANTUM_GENERATOR.get(), containerId);
        this.blockEntity = blockEntity;
        this.data = data;
        this.blockPos = blockPos;
        addPlayerInventory(playerInventory, 8, 110);
        this.addDataSlots(this.data);
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public int getProduction() {
        return SyncedData.combine(this.data.get(0), this.data.get(1));
    }

    public int getGeneratorTier() {
        return this.data.get(2);
    }

    public boolean isActive() {
        return this.data.get(3) != 0;
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
        return ItemStack.EMPTY;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (this.blockEntity == null || !this.stillValid(player)) {
            return false;
        }
        this.blockEntity.handleGuiEvent(id);
        this.broadcastChanges();
        return true;
    }

    private void addPlayerInventory(Inventory inventory, int x, int y) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new net.minecraft.world.inventory.Slot(inventory, column + row * 9 + 9, x + column * 18, y + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            this.addSlot(new net.minecraft.world.inventory.Slot(inventory, column, x + column * 18, y + 58));
        }
    }

    private static MenuContext getMenuContext(Inventory inventory, FriendlyByteBuf buffer) {
        BlockPos blockPos = buffer.readBlockPos();
        BlockEntity blockEntity = inventory.player.level().getBlockEntity(blockPos);
        return new MenuContext(blockEntity instanceof QuantumGeneratorBlockEntity quantumGenerator ? quantumGenerator : null, blockPos);
    }

    private record MenuContext(QuantumGeneratorBlockEntity blockEntity, BlockPos blockPos) {
    }
}
