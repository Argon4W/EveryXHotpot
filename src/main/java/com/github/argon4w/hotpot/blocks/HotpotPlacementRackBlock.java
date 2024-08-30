package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.items.HotpotPlacementBlockItem;
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
import org.jetbrains.annotations.Nullable;

public class HotpotPlacementRackBlock extends BaseEntityBlock {
    public static final VoxelShape SHAPE = Shapes.or(box(0.0f, 0.0f, 0.0f, 16.0f, 2.5f, 16.0f), box(0.0f, 10.5f, 0.0f, 16.0f, 12.5f, 16.0f));
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
    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        LevelBlockPos selfPos = new LevelBlockPos(level, pos);

        if (!(selfPos.getBlockEntity() instanceof HotpotPlacementRackBlockEntity hotpotPlacementRackBlockEntity)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (itemStack.getItem() instanceof HotpotPlacementBlockItem<?> hotpotPlacementBlockItem && hotpotPlacementBlockItem.canPlace(player, hand, selfPos)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!selfPos.isServerSide()) {
            return ItemInteractionResult.sidedSuccess(true);
        }

        int hitPos = HotpotPlacementBlockItem.getHitPos(result);
        int layer = HotpotPlacementBlockItem.getLayer(result);

        hotpotPlacementRackBlockEntity.interact(hitPos, layer + hotpotPlacementRackBlockEntity.getLayerOffset(), player, hand, itemStack, selfPos);

        return ItemInteractionResult.sidedSuccess(false);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader levelReader, BlockPos pos, Player player) {
        if (!(levelReader instanceof Level level)) {
            return super.getCloneItemStack(state, target, levelReader, pos, player);
        }

        LevelBlockPos selfPos = new LevelBlockPos(level, pos);

        if (!(selfPos.getBlockEntity() instanceof HotpotPlacementRackBlockEntity hotpotPlacementRackBlockEntity)) {
            return super.getCloneItemStack(state, target, levelReader, pos, player);
        }

        int hitPos = HotpotPlacementBlockItem.getHitPos(pos, target.getLocation());
        int layer = HotpotPlacementBlockItem.getLayer(pos, target.getLocation());

        return hotpotPlacementRackBlockEntity.getPlacementInPosAndLayer(hitPos, layer).getCloneItemStack(hotpotPlacementRackBlockEntity, selfPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean b) {
        if (state.is(newState.getBlock())) {
            return;
        }

        if (level.getBlockEntity(pos) instanceof HotpotPlacementRackBlockEntity hotpotPlacementRackBlockEntity) {
            hotpotPlacementRackBlockEntity.onRemove(new LevelBlockPos(level, pos));
        }

        super.onRemove(state, level, pos, newState, b);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public  RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return MapCodec.unit(HotpotPlacementRackBlock::new);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState blockState) {
        return new HotpotPlacementRackBlockEntity(pos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, HotpotModEntry.HOTPOT_PLACEMENT_RACK_BLOCK_ENTITY.get(), HotpotPlacementRackBlockEntity::tick);
    }
}
