package com.github.argon4w.everyxhotpot;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;

public class HotpotBlock extends BaseEntityBlock {
    public HotpotBlock() {
        super(BlockBehaviour.Properties.of()
                .noOcclusion()
                .mapColor(MapColor.METAL)
                .sound(SoundType.COPPER)
                .requiresCorrectToolForDrops()
                .lightLevel((blockState) -> 15)
                .strength(1.5f, 6.0f));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HotpotBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.MODEL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        BlockEntity entity = level.getBlockEntity(pos);

        if (entity instanceof HotpotBlockEntity hotpotBlockEntity && result.getDirection() != Direction.DOWN) {
            ItemStack stack = player.getItemInHand(hand);
            int hitSection = getHitSection(result);

            if (stack.isEmpty()) {
                if (!level.isClientSide) {
                    hotpotBlockEntity.dropFood(hitSection, level, pos);
                }

                return InteractionResult.SUCCESS;
            } else {
                if (!level.isClientSide && hotpotBlockEntity.placeFood(hitSection, player.getAbilities().instabuild ? stack.copy() : stack)) {
                    return InteractionResult.SUCCESS;
                }

                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.PASS;
    }

    private int getHitSection(BlockHitResult result) {
        BlockPos blockpos = result.getBlockPos().relative(Direction.UP);
        Vec3 vec3 = result.getLocation().subtract(blockpos.getX(), blockpos.getY(), blockpos.getZ());
        double x = vec3.x() - 0.5f;
        double z = vec3.z() - 0.5f;

        double sectionSize = (360f / 8f);
        double degree = Math.atan2(x, z) / Math.PI * 180f + sectionSize / 2f;
        degree = degree < 0f ? degree + 360f : degree;

        return (int) Math.floor(degree / sectionSize);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : createTickerHelper(blockEntityType, HotpotModEntry.HOTPOT_BLOCK_ENTITY.get(), HotpotBlockEntity::tick);
    }
}
