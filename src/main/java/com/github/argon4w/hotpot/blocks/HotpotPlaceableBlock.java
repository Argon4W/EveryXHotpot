package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.items.HotpotPlaceableBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HotpotPlaceableBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 3, 16);

    public HotpotPlaceableBlock() {
        super(Properties.of(Material.METAL)
                .noOcclusion()
                .sound(SoundType.COPPER)
                .strength(0.5f));
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return SHAPE;
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        BlockPosWithLevel selfPos = new BlockPosWithLevel(level, pos);
        ItemStack itemStack = player.getItemInHand(hand);

        if (selfPos.getBlockEntity() instanceof HotpotPlaceableBlockEntity hotpotPlaceableBlockEntity) {
            if (itemStack.getItem() instanceof HotpotPlaceableBlockItem hotpotPlaceableBlockItem && hotpotPlaceableBlockItem.shouldPlace(player, hand, selfPos)) {
                return InteractionResult.PASS;
            }

            if (selfPos.isServerSide()) {
                int hitPos = HotpotPlaceableBlockEntity.getHitPos(result);
                hotpotPlaceableBlockEntity.interact(hitPos, player, hand, itemStack, selfPos);
            }

            return InteractionResult.sidedSuccess(!selfPos.isServerSide());
        }

        return InteractionResult.PASS;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter blockGetter, BlockPos pos, Player player) {
        if (!(blockGetter instanceof Level)) return super.getCloneItemStack(state, target, blockGetter, pos, player);

        BlockPosWithLevel selfPos = new BlockPosWithLevel((Level) blockGetter, pos);

        if (selfPos.getBlockEntity() instanceof HotpotPlaceableBlockEntity hotpotPlaceableBlockEntity) {
            int hitPos = HotpotPlaceableBlockEntity.getHitPos(pos, target.getLocation());

            return hotpotPlaceableBlockEntity.getPlaceableInPos(hitPos).getCloneItemStack(hotpotPlaceableBlockEntity, selfPos);
        }

        return super.getCloneItemStack(state, target, blockGetter, pos, player);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean b) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof HotpotPlaceableBlockEntity hotpotPlaceableBlockEntity) {
            hotpotPlaceableBlockEntity.onRemove(new BlockPosWithLevel(level, pos));
        }

        super.onRemove(state, level, pos, newState, b);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState blockState) {
        return new HotpotPlaceableBlockEntity(pos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, HotpotModEntry.HOTPOT_PLACEABLE_BLOCK_ENTITY.get(), HotpotPlaceableBlockEntity::tick);
    }
}
