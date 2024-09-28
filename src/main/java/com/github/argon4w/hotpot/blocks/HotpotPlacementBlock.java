package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.api.items.HotpotPlacementBlockItem;
import com.github.argon4w.hotpot.placements.coords.HotpotPlacementCoords;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HotpotPlacementBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 2, 16);

    public HotpotPlacementBlock() {
        super(Properties.of()
                .forceSolidOn()
                .noCollission()
                .noOcclusion()
                .mapColor(MapColor.COLOR_GRAY)
                .sound(SoundType.COPPER)
                .isViewBlocking((pState, pLevel, pPos) -> false)
                .strength(0.5f));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        LevelBlockPos blockPos = new LevelBlockPos(level, pos);

        if (itemStack.getItem() instanceof HotpotPlacementBlockItem<?> hotpotPlacementBlockItem && hotpotPlacementBlockItem.canPlace(player, hand, blockPos)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!blockPos.isServerSide()) {
            return ItemInteractionResult.sidedSuccess(true);
        }

        int position = HotpotPlacementBlockItem.getPosition(result);
        HotpotPlacementCoords.interactNearbyPositions(blockPos, player, hand, itemStack, position, 0);

        return ItemInteractionResult.sidedSuccess(false);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader levelReader, BlockPos pos, Player player) {
        if (!(levelReader instanceof Level level)) {
            return super.getCloneItemStack(state, target, levelReader, pos, player);
        }

        LevelBlockPos blockPos = new LevelBlockPos(level, pos);

        if (!(blockPos.getBlockEntity() instanceof HotpotPlacementBlockEntity hotpotPlacementBlockEntity)) {
            return super.getCloneItemStack(state, target, levelReader, pos, player);
        }

        int position = HotpotPlacementBlockItem.getPosition(pos, target.getLocation());
        int index = hotpotPlacementBlockEntity.getPlacementIndexInPos(position);

        return index < 0 ? ItemStack.EMPTY : hotpotPlacementBlockEntity.getPlacements().get(index).getCloneItemStack(hotpotPlacementBlockEntity, blockPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean b) {
        if (state.is(newState.getBlock())) {
            return;
        }

        if (level.getBlockEntity(pos) instanceof HotpotPlacementBlockEntity hotpotPlacementBlockEntity) {
            hotpotPlacementBlockEntity.onRemove(new LevelBlockPos(level, pos));
        }

        super.onRemove(state, level, pos, newState, b);
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState blockState) {
        return new HotpotPlacementBlockEntity(pos, blockState);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return MapCodec.unit(HotpotPlacementBlock::new);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, HotpotModEntry.HOTPOT_PLACEMENT_BLOCK_ENTITY.get(), HotpotPlacementBlockEntity::tick);
    }
}
