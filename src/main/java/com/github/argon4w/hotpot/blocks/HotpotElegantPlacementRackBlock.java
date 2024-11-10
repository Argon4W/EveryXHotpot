package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.fancytoys.LevelBlockPos;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.api.items.HotpotPlacementBlockItem;
import com.github.argon4w.hotpot.placements.coords.HotpotPlacementCoords;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HotpotElegantPlacementRackBlock extends BaseEntityBlock {

    public static final VoxelShape SHAPE_NORTH = Shapes.or(
            box(8.0f, 2.0f, 0.0f, 16.0f, 3.0f, 16.0f),
            box(8.0f, 11.0f, 0.0f, 16.0f, 12.0f, 16.0f),
            box(0.0f, 0.0f, 0.0f, 16.0f, 2.0f, 16.0f));
    public static final VoxelShape SHAPE_SOUTH = Shapes.or(
            box(0.0f, 2.0f, 0.0f, 8.0f, 3.0f, 16.0f),
            box(0.0f, 11.0f, 0.0f, 8.0f, 12.0f, 16.0f),
            box(0.0f, 0.0f, 0.0f, 16.0f, 2.0f, 16.0f));
    public static final VoxelShape SHAPE_EAST = Shapes.or(
            box(0.0f, 2.0f, 8.0f, 16.0f, 3.0f, 16.0f),
            box(0.0f, 11.0f, 8.0f, 16.0f, 12.0f, 16.0f),
            box(0.0f, 0.0f, 0.0f, 16.0f, 2.0f, 16.0f));
    public static final VoxelShape SHAPE_WEST = Shapes.or(
            box(0.0f, 2.0f, 0.0f, 16.0f, 3.0f, 8.0f),
            box(0.0f, 11.0f, 0.0f, 16.0f, 12.0f, 8.0f),
            box(0.0f, 0.0f, 0.0f, 16.0f, 2.0f, 16.0f));

    public static final VoxelShape[] SHAPES_BY_INDEX = {
            SHAPE_SOUTH,
            SHAPE_WEST,
            SHAPE_NORTH,
            SHAPE_EAST
    };

    private static final Object2IntMap<BlockState> STATE_TO_INDEX = new Object2IntOpenHashMap<>();
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public HotpotElegantPlacementRackBlock() {
        super(Properties.of()
                .forceSolidOn()
                .noOcclusion()
                .mapColor(MapColor.COLOR_GRAY)
                .sound(SoundType.COPPER)
                .isViewBlocking((pState, pLevel, pPos) -> false)
                .strength(0.5f));

        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(
            @NotNull ItemStack itemStack,
            @NotNull BlockState state,
            @NotNull Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull InteractionHand hand,
            @NotNull BlockHitResult result) {
        LevelBlockPos blockPos = new LevelBlockPos(level, pos);

        if (!(blockPos.getBlockEntity() instanceof HotpotElegantPlacementRackBlockEntity)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (HotpotPlacementBlockItem.shouldPass(itemStack, player, hand, blockPos)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!blockPos.isServerSide()) {
            return ItemInteractionResult.sidedSuccess(true);
        }

        int position = HotpotPlacementBlockItem.getPosition(result);
        int layer = HotpotElegantPlacementRackBlockEntity.getLayerFromBlockHitResult(result);

        HotpotPlacementCoords.interactNearbyPositions(blockPos, player, hand, itemStack, position, layer);

        return ItemInteractionResult.sidedSuccess(false);
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(
            @NotNull BlockState state,
            @NotNull HitResult target,
            @NotNull LevelReader levelReader,
            @NotNull BlockPos pos,
            @NotNull Player player) {
        if (!(levelReader instanceof Level level)) {
            return super.getCloneItemStack(state, target, levelReader, pos, player);
        }

        LevelBlockPos blockPos = new LevelBlockPos(level, pos);

        if (!(blockPos.getBlockEntity() instanceof HotpotElegantPlacementRackBlockEntity blockEntity)) {
            return super.getCloneItemStack(state, target, levelReader, pos, player);
        }

        int position = HotpotPlacementBlockItem.getPosition(pos, target.getLocation());
        int layer = HotpotElegantPlacementRackBlockEntity.getLayerFromHitResult(target, pos);
        int index = blockEntity.getPlacementIndexInPosAndLayer(position, layer);

        if (index < 0) {
            return super.getCloneItemStack(state, target, levelReader, pos, player);
        }

        return blockEntity.getPlacements(layer).get(index).getCloneItemStack(blockEntity, blockPos);
    }

    @Override
    public void onRemove(
            BlockState state,
            @NotNull Level level,
            @NotNull BlockPos pos,
            BlockState newState,
            boolean movedByPiston) {
        LevelBlockPos blockPos = new LevelBlockPos(level, pos);

        if (state.is(newState.getBlock())) {
            return;
        }

        if (blockPos.getBlockEntity() instanceof HotpotElegantPlacementRackBlockEntity blockEntity) {
            blockEntity.onRemove(blockPos);
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Nullable @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            @NotNull BlockState blockState,
            @NotNull BlockEntityType<T> blockEntityType) {
        return level.isClientSide
                ? null
                : createTickerHelper(blockEntityType, HotpotModEntry.HOTPOT_ELEGANT_PLACEMENT_RACK_BLOCK_ENTITY.get(), HotpotElegantPlacementRackBlockEntity::tick);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected @NotNull VoxelShape getShape(
            @NotNull BlockState state,
            @NotNull BlockGetter blockGetter,
            @NotNull BlockPos pos,
            @NotNull CollisionContext context) {
        return SHAPES_BY_INDEX[STATE_TO_INDEX.computeIntIfAbsent(state, s -> s.getValue(FACING).get2DDataValue())];
    }

    @Override
    @SuppressWarnings("deprecation")
    protected @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return MapCodec.unit(HotpotElegantPlacementRackBlock::new);
    }

    @Nullable @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        return new HotpotElegantPlacementRackBlockEntity(pos, blockState);
    }
}
