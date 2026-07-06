package com.refactoredsolarpanels.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class DoubleStoneSlabItem extends Item {
    public DoubleStoneSlabItem() {
        super(new Properties());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        BlockPlaceContext placeContext = new BlockPlaceContext(context);
        if (!level.getBlockState(context.getClickedPos()).canBeReplaced(placeContext)) {
            pos = context.getClickedPos().relative(context.getClickedFace());
        } else {
            pos = context.getClickedPos();
        }

        BlockState state = Blocks.SMOOTH_STONE.defaultBlockState();
        if (!level.mayInteract(context.getPlayer(), pos) || !state.canSurvive(level, pos)) {
            return InteractionResult.FAIL;
        }
        if (!level.isClientSide) {
            level.setBlock(pos, state, Block.UPDATE_ALL);
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
