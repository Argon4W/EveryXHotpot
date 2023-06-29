package com.github.argon4w.everyxhotpot;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;

public class HotpotBlock extends BaseEntityBlock {
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty WEST = BooleanProperty.create("west");

    public static final BooleanProperty WEST_NORTH = BooleanProperty.create("west_north");
    public static final BooleanProperty NORTH_EAST = BooleanProperty.create("north_east");
    public static final BooleanProperty EAST_SOUTH = BooleanProperty.create("east_south");
    public static final BooleanProperty SOUTH_WEST = BooleanProperty.create("south_west");

    public HotpotBlock() {
        super(BlockBehaviour.Properties.of()
                .noOcclusion()
                .mapColor(MapColor.METAL)
                .sound(SoundType.COPPER)
                .requiresCorrectToolForDrops()
                .lightLevel((blockState) -> 15)
                .strength(1.5f, 6.0f));

        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
                .setValue(WEST_NORTH, false)
                .setValue(NORTH_EAST, false)
                .setValue(EAST_SOUTH, false)
                .setValue(SOUTH_WEST, false)
        );
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

    private BlockState updateState(BlockState state, BlockPos pos, BlockGetter getter) {
        BlockPos north = pos.north();
        BlockPos south = pos.south();
        BlockPos east = pos.east();
        BlockPos west = pos.west();

        BlockPos west_north = north.west();
        BlockPos north_east = east.north();
        BlockPos east_south = south.east();
        BlockPos south_west = west.south();

        boolean northValue = getter.getBlockState(north).is(HotpotModEntry.HOTPOT_BLOCK.get());
        boolean southValue = getter.getBlockState(south).is(HotpotModEntry.HOTPOT_BLOCK.get());
        boolean eastValue = getter.getBlockState(east).is(HotpotModEntry.HOTPOT_BLOCK.get());
        boolean westValue = getter.getBlockState(west).is(HotpotModEntry.HOTPOT_BLOCK.get());

        return state
                .setValue(NORTH, northValue)
                .setValue(SOUTH, southValue)
                .setValue(EAST, eastValue)
                .setValue(WEST, westValue)
                .setValue(WEST_NORTH, westValue && northValue && getter.getBlockState(west_north).is(HotpotModEntry.HOTPOT_BLOCK.get()))
                .setValue(NORTH_EAST, northValue && eastValue && getter.getBlockState(north_east).is(HotpotModEntry.HOTPOT_BLOCK.get()))
                .setValue(EAST_SOUTH, eastValue && southValue && getter.getBlockState(east_south).is(HotpotModEntry.HOTPOT_BLOCK.get()))
                .setValue(SOUTH_WEST, southValue && westValue && getter.getBlockState(south_west).is(HotpotModEntry.HOTPOT_BLOCK.get()));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return updateState(super.getStateForPlacement(context), context.getClickedPos(), context.getLevel());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction direction, BlockState nearbyState, LevelAccessor accessor, BlockPos pos, BlockPos nearbyPos) {
        return updateState(state, pos, accessor);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, WEST_NORTH, NORTH_EAST, EAST_SOUTH, SOUTH_WEST);
    }
}
