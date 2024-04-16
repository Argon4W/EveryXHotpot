package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.items.HotpotPlacementBlockItem;
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
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HotpotPlacementBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 3, 16);

    public HotpotPlacementBlock() {
        super(Properties.of()
                .forceSolidOn()
                .noOcclusion()
                .mapColor(MapColor.COLOR_GRAY)
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
        LevelBlockPos selfPos = new LevelBlockPos(level, pos);
        ItemStack itemStack = player.getItemInHand(hand);

        if (selfPos.getBlockEntity() instanceof HotpotPlacementBlockEntity hotpotPlacementBlockEntity) {
            if (itemStack.is(itemHolder -> itemHolder.get() instanceof HotpotPlacementBlockItem hotpotPlacementBlockItem && hotpotPlacementBlockItem.canPlace(player, hand, selfPos))) {
                return InteractionResult.PASS;
            }

            if (selfPos.isServerSide()) {
                int hitPos = HotpotPlacementBlockEntity.getHitPos(result);
                hotpotPlacementBlockEntity.interact(hitPos, player, hand, itemStack, selfPos);
            }

            return InteractionResult.sidedSuccess(!selfPos.isServerSide());
        }

        return InteractionResult.PASS;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter blockGetter, BlockPos pos, Player player) {
        if (!(blockGetter instanceof Level)) return super.getCloneItemStack(state, target, blockGetter, pos, player);

        LevelBlockPos selfPos = new LevelBlockPos((Level) blockGetter, pos);

        if (selfPos.getBlockEntity() instanceof HotpotPlacementBlockEntity hotpotPlacementBlockEntity) {
            int hitPos = HotpotPlacementBlockEntity.getHitPos(pos, target.getLocation());

            return hotpotPlacementBlockEntity.getPlacementInPos(hitPos).getCloneItemStack(hotpotPlacementBlockEntity, selfPos);
        }

        return super.getCloneItemStack(state, target, blockGetter, pos, player);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean b) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof HotpotPlacementBlockEntity hotpotPlacementBlockEntity) {
            hotpotPlacementBlockEntity.onRemove(new LevelBlockPos(level, pos));
        }

        super.onRemove(state, level, pos, newState, b);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState blockState) {
        return new HotpotPlacementBlockEntity(pos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, HotpotModEntry.HOTPOT_PLACEMENT_BLOCK_ENTITY.get(), HotpotPlacementBlockEntity::tick);
    }
}
