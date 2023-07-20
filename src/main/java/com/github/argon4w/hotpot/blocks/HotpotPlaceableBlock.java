package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.items.HotpotPlateBlockItem;
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

public class HotpotPlateBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 3, 16);

    public HotpotPlateBlock() {
        super(Properties.of()
                .noOcclusion()
                .mapColor(MapColor.COLOR_GRAY)
                .sound(SoundType.COPPER)
                .strength(3.0F, 6.0F));
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

        if (itemStack.is(itemHolder -> itemHolder.get() instanceof HotpotPlateBlockItem)) {
            return InteractionResult.PASS;
        }

        if (selfPos.getBlockEntity() instanceof HotpotPlateBlockEntity hotpotPlateBlockEntity) {
            if (selfPos.isServerSide()) {
                int hitSlot = HotpotPlateBlockEntity.getHitSlot(result);
                hotpotPlateBlockEntity.interact(hitSlot, player, hand, itemStack, selfPos);
            }

            return InteractionResult.sidedSuccess(!selfPos.isServerSide());
        }

        return InteractionResult.PASS;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter blockGetter, BlockPos pos, Player player) {
        if (!(blockGetter instanceof Level)) return super.getCloneItemStack(state, target, blockGetter, pos, player);

        BlockPosWithLevel selfPos = new BlockPosWithLevel((Level) blockGetter, pos);

        if (selfPos.getBlockEntity() instanceof HotpotPlateBlockEntity hotpotPlateBlockEntity) {
            int hitSlot = HotpotPlateBlockEntity.getHitSlot(pos, target.getLocation());

            return hotpotPlateBlockEntity.getPlateInSlot(hitSlot).getCloneItemStack(hotpotPlateBlockEntity, selfPos);
        }

        return super.getCloneItemStack(state, target, blockGetter, pos, player);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState blockState) {
        return new HotpotPlateBlockEntity(pos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, HotpotModEntry.HOTPOT_PLATE_BLOCK_ENTITY.get(), HotpotPlateBlockEntity::tick);
    }
}
