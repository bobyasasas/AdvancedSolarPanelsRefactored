package com.refactoredsolarpanels.compat.buildcraft;

import com.refactoredsolarpanels.config.AdvancedSolarCommonConfig;
import ic2.api.tile.IWrenchAble;
import java.util.Collections;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

public final class EnergyConverterBlock extends BaseEntityBlock implements IWrenchAble {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private final EnergyConverterType converterType;

    public EnergyConverterBlock(EnergyConverterType converterType) {
        super(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(3.5F, 6.0F).requiresCorrectToolForDrops());
        this.converterType = converterType;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public EnergyConverterType getConverterType() {
        return this.converterType;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyConverterBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, BuildCraftConverters.ENERGY_CONVERTER.get(), EnergyConverterBlockEntity::serverTick);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public Direction getFacing(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.hasProperty(FACING) ? state.getValue(FACING) : Direction.NORTH;
    }

    @Override
    public boolean setFacing(Level level, BlockPos pos, Direction direction, Player player) {
        if (!direction.getAxis().isHorizontal()) {
            return false;
        }
        level.setBlock(pos, level.getBlockState(pos).setValue(FACING, direction), Block.UPDATE_ALL);
        return true;
    }

    @Override
    public boolean wrenchCanRemove(Level level, BlockPos pos, Player player) {
        return true;
    }

    @Override
    public List<ItemStack> getWrenchDrops(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity, Player player, int fortune) {
        return Collections.singletonList(this.asItem().getDefaultInstance());
    }

    @Override
    public void appendHoverText(ItemStack stack, BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        String directionKey = this.converterType.isElectricEngine() ? "eu_to_mj" : "mj_to_eu";
        tooltip.add(Component.translatable(
                "tooltip.advanced_solar_panels_refactored.converter." + directionKey,
                format(this.converterType.getEuRate()),
                format(this.converterType.getMjRate())
        ).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.advanced_solar_panels_refactored.converter.facing").withStyle(ChatFormatting.DARK_GRAY));
        if (!AdvancedSolarCommonConfig.isBuildCraftConverterEnabled(this.converterType.getId())) {
            tooltip.add(Component.translatable("tooltip.advanced_solar_panels_refactored.converter.disabled").withStyle(ChatFormatting.RED));
        }
    }

    private static String format(double value) {
        return value == Math.rint(value) ? Long.toString(Math.round(value)) : Double.toString(value);
    }
}
