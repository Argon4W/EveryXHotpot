package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.items.HotpotPlaceableBlockItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class HotpotPlaceableBlock extends Block {
    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 3, 16);

    public HotpotPlaceableBlock() {
        super(Properties.of(Material.STONE)
                .noOcclusion()
                .sound(SoundType.STONE)
                .strength(0.5f));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState p_60555_, IBlockReader p_60556_, BlockPos p_60557_, ISelectionContext p_60558_) {
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        BlockPosWithLevel selfPos = new BlockPosWithLevel(level, pos);
        ItemStack itemStack = player.getItemInHand(hand);

        if (selfPos.getBlockEntity() instanceof HotpotPlaceableBlockEntity) {
            if (itemStack.getItem() instanceof HotpotPlaceableBlockItem && ((HotpotPlaceableBlockItem) itemStack.getItem()).shouldPlace(player, hand, selfPos)) {
                return ActionResultType.PASS;
            }

            if (selfPos.isServerSide()) {
                int hitPos = HotpotPlaceableBlockEntity.getHitPos(result);
                ((HotpotPlaceableBlockEntity) selfPos.getBlockEntity()).interact(hitPos, player, hand, itemStack, selfPos);
            }

            return ActionResultType.sidedSuccess(!selfPos.isServerSide());
        }

        return ActionResultType.PASS;
    }


    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader blockGetter, BlockPos pos, PlayerEntity player) {
        if (!(blockGetter instanceof World)) return super.getPickBlock(state, target, blockGetter, pos, player);

        BlockPosWithLevel selfPos = new BlockPosWithLevel((World) blockGetter, pos);

        if (selfPos.getBlockEntity() instanceof HotpotPlaceableBlockEntity) {
            HotpotPlaceableBlockEntity hotpotPlaceableBlockEntity = (HotpotPlaceableBlockEntity) selfPos.getBlockEntity();
            int hitPos = HotpotPlaceableBlockEntity.getHitPos(pos, target.getLocation());

            return hotpotPlaceableBlockEntity.getPlaceableInPos(hitPos).getCloneItemStack(hotpotPlaceableBlockEntity, selfPos);
        }

        return super.getPickBlock(state, target, blockGetter, pos, player);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean b) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof HotpotPlaceableBlockEntity) {
            ((HotpotPlaceableBlockEntity) level.getBlockEntity(pos)).onRemove(new BlockPosWithLevel(level, pos));
        }

        super.onRemove(state, level, pos, newState, b);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new HotpotPlaceableBlockEntity();
    }
}
