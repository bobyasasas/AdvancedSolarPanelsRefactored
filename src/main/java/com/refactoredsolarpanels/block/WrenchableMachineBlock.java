package com.refactoredsolarpanels.block;

import ic2.api.tile.IWrenchAble;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

interface WrenchableMachineBlock extends IWrenchAble {
    @Override
    default Direction getFacing(Level level, BlockPos pos) {
        return Direction.DOWN;
    }

    @Override
    default boolean setFacing(Level level, BlockPos pos, Direction direction, Player player) {
        return false;
    }

    @Override
    default boolean wrenchCanRemove(Level level, BlockPos pos, Player player) {
        return true;
    }

    @Override
    default List<ItemStack> getWrenchDrops(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity, Player player, int fortune) {
        return Collections.singletonList(state.getBlock().asItem().getDefaultInstance());
    }
}
