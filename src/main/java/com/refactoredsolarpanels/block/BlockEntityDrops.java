package com.refactoredsolarpanels.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandlerModifiable;

final class BlockEntityDrops {
    private BlockEntityDrops() {
    }

    static void dropContents(Level level, BlockPos pos, IItemHandlerModifiable inventory) {
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                dropStack(level, pos, stack);
                inventory.setStackInSlot(slot, ItemStack.EMPTY);
            }
        }
    }

    static void dropStack(Level level, BlockPos pos, ItemStack stack) {
        if (!stack.isEmpty()) {
            Containers.dropItemStack(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack.copy());
        }
    }
}
