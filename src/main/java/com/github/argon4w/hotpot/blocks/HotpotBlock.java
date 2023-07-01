package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.contents.HotpotItemStackContent;
import com.github.argon4w.hotpot.contents.HotpotPlayerContent;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
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

    private final VoxelShape[] shapesByIndex = makeShapes();
    private final Object2IntMap<BlockState> stateToIndex = new Object2IntOpenHashMap<>();

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

    private VoxelShape[] makeShapes() {
        VoxelShape base = box(0, 0, 0, 16, 8, 16);
        VoxelShape south = box(0, 8, 15, 16, 16, 16); //south(2^0)
        VoxelShape west = box(0, 8, 0, 1, 16, 16); //west(2^1)
        VoxelShape north = box(0, 8, 0, 16, 16, 1); //north(2^2)
        VoxelShape east = box(15, 8, 0, 16, 16, 16); //east(2^3)

        VoxelShape[] faces = {
                Shapes.empty(), //0000 (0)
                south, //0001 (1)
                west, //0010 (2)
                Shapes.or(south, west), //0011 (3)
                north, //0100 (4)
                Shapes.or(north, south), //0101 (5)
                Shapes.or(north, west), //0110 (6)
                Shapes.or(north, west, south), //0111 (7)
                east, //1000 (8)
                Shapes.or(east, south), //1001 (9)
                Shapes.or(east, west), //1010 (10)
                Shapes.or(east, west, south), //1011 (11)
                Shapes.or(east, north), //1100 (12)
                Shapes.or(east, north, south), //1101 (13)
                Shapes.or(east, north, west), //1110 (14)
                Shapes.or(east, north, west, south) //1111 (15)
        };

        for (int i = 0; i < faces.length; i ++) {
            faces[i] = Shapes.or(base, faces[i]);
        }

        return faces;
    }

    private BlockState updateState(BlockState state, BlockPos pos, BlockGetter getter) {
        BlockPos north = pos.north();
        BlockPos south = pos.south();
        BlockPos east = pos.east();
        BlockPos west = pos.west();

        BlockPos westNorth = north.west();
        BlockPos northEast = east.north();
        BlockPos eastSouth = south.east();
        BlockPos southWest = west.south();

        boolean northValue = getter.getBlockState(north).is(HotpotModEntry.HOTPOT_BLOCK.get());
        boolean southValue = getter.getBlockState(south).is(HotpotModEntry.HOTPOT_BLOCK.get());
        boolean eastValue = getter.getBlockState(east).is(HotpotModEntry.HOTPOT_BLOCK.get());
        boolean westValue = getter.getBlockState(west).is(HotpotModEntry.HOTPOT_BLOCK.get());

        return state
                .setValue(NORTH, northValue)
                .setValue(SOUTH, southValue)
                .setValue(EAST, eastValue)
                .setValue(WEST, westValue)
                .setValue(WEST_NORTH, westValue && northValue && getter.getBlockState(westNorth).is(HotpotModEntry.HOTPOT_BLOCK.get()))
                .setValue(NORTH_EAST, northValue && eastValue && getter.getBlockState(northEast).is(HotpotModEntry.HOTPOT_BLOCK.get()))
                .setValue(EAST_SOUTH, eastValue && southValue && getter.getBlockState(eastSouth).is(HotpotModEntry.HOTPOT_BLOCK.get()))
                .setValue(SOUTH_WEST, southValue && westValue && getter.getBlockState(southWest).is(HotpotModEntry.HOTPOT_BLOCK.get()));
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

    @SuppressWarnings("deprecation")
    private int getShapeIndex(BlockState state) {
        return stateToIndex.computeIntIfAbsent(state, (blockState) -> {
            int index = 0;

            index = blockState.getValue(SOUTH) ? index : index | indexFor(Direction.SOUTH);
            index = blockState.getValue(WEST) ? index : index | indexFor(Direction.WEST);
            index = blockState.getValue(NORTH) ? index : index | indexFor(Direction.NORTH);
            index = blockState.getValue(EAST) ? index : index | indexFor(Direction.EAST);

            return index;
        });
    }

    private static int indexFor(Direction direction) {
        return 1 << direction.get2DDataValue();
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        BlockEntity entity = level.getBlockEntity(pos);

        if (entity instanceof HotpotBlockEntity hotpotBlockEntity) {
            ItemStack stack = player.getItemInHand(hand);
            int hitSection = getHitSection(result);

            if (stack.isEmpty()) {
                if (!level.isClientSide) {
                    hotpotBlockEntity.dropContent(hitSection, level, pos);
                }

                return InteractionResult.SUCCESS;
            } else {
                int cookingTime = HotpotBlockEntity.quickCheck.getRecipeFor(new SimpleContainer(stack), level).map(AbstractCookingRecipe::getCookingTime).orElse(-1);
                if (!level.isClientSide && hotpotBlockEntity.placeContent(hitSection, new HotpotItemStackContent((player.getAbilities().instabuild ? stack.copy() : stack).split(1), cookingTime, 0), level, pos)) {
                    return InteractionResult.SUCCESS;
                }

                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);

        entity.hurt(new DamageSource(HotpotModEntry.IN_HOTPOT_DAMAGE_TYPE.apply(level), new Vec3(pos.getX(), pos.getY(), pos.getZ())), 3f);
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction direction, BlockState nearbyState, LevelAccessor accessor, BlockPos pos, BlockPos nearbyPos) {
        return updateState(state, pos, accessor);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : createTickerHelper(blockEntityType, HotpotModEntry.HOTPOT_BLOCK_ENTITY.get(), HotpotBlockEntity::tick);
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return shapesByIndex[getShapeIndex(state)];
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return updateState(super.getStateForPlacement(context), context.getClickedPos(), context.getLevel());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, WEST_NORTH, NORTH_EAST, EAST_SOUTH, SOUTH_WEST);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HotpotBlockEntity(pos, state);
    }

    @NotNull
    @Override
    public  RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.MODEL;
    }
}
