package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.api.items.HotpotPlacementBlockItem;
import com.mojang.serialization.MapCodec;
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
import net.minecraft.world.level.block.state.BlockBehaviour;
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

public class HotpotPlacementRackBlock extends BaseEntityBlock {
    public static final VoxelShape SHAPE =
            Shapes.or(box(0.0f, 0.0f, 0.0f, 16.0f, 2.5f, 16.0f), box(0.0f, 10.5f, 0.0f, 16.0f, 12.5f, 16.0f));
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public HotpotPlacementRackBlock() {
        super(BlockBehaviour.Properties.of()
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

        if (!(blockPos.getBlockEntity() instanceof HotpotPlacementRackBlockEntity hotpotPlacementRackBlockEntity)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (itemStack.getItem() instanceof HotpotPlacementBlockItem<?> hotpotPlacementBlockItem
                && hotpotPlacementBlockItem.canPlace(player, hand, blockPos)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!blockPos.isServerSide()) {
            return ItemInteractionResult.sidedSuccess(true);
        }

        int position = HotpotPlacementBlockItem.getPosition(result);
        int layer = HotpotPlacementRackBlockEntity.getLayerFromBlockHitResult(result);

        hotpotPlacementRackBlockEntity.interact(position, layer, player, hand, itemStack, blockPos);

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

        if (!(blockPos.getBlockEntity() instanceof HotpotPlacementRackBlockEntity hotpotPlacementRackBlockEntity)) {
            return super.getCloneItemStack(state, target, levelReader, pos, player);
        }

        int position = HotpotPlacementBlockItem.getPosition(pos, target.getLocation());
        int layer = HotpotPlacementRackBlockEntity.getLayerFromHitResult(target, pos);

        return hotpotPlacementRackBlockEntity
                .getPlacementInPosAndLayer(position, layer)
                .getCloneItemStack(hotpotPlacementRackBlockEntity, blockPos);
    }

    @Override
    public void onRemove(
            BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean b) {
        if (state.is(newState.getBlock())) {
            return;
        }

        if (level.getBlockEntity(pos) instanceof HotpotPlacementRackBlockEntity hotpotPlacementRackBlockEntity) {
            hotpotPlacementRackBlockEntity.onRemove(new LevelBlockPos(level, pos));
        }

        super.onRemove(state, level, pos, newState, b);
    }

    @Nullable @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, @NotNull BlockState blockState, @NotNull BlockEntityType<T> blockEntityType) {
        return level.isClientSide
                ? null
                : createTickerHelper(
                        blockEntityType,
                        HotpotModEntry.HOTPOT_PLACEMENT_RACK_BLOCK_ENTITY.get(),
                        HotpotPlacementRackBlockEntity::tick);
    }

    @Override
    protected @NotNull VoxelShape getShape(
            @NotNull BlockState state,
            @NotNull BlockGetter blockGetter,
            @NotNull BlockPos pos,
            @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState()
                .setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return MapCodec.unit(HotpotPlacementRackBlock::new);
    }

    @Nullable @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        return new HotpotPlacementRackBlockEntity(pos, blockState);
    }
}
