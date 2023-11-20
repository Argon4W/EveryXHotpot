package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.IArmorVanishable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class HotpotBlock extends Block implements IArmorVanishable {
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty WEST = BooleanProperty.create("west");

    public static final BooleanProperty WEST_NORTH = BooleanProperty.create("west_north");
    public static final BooleanProperty NORTH_EAST = BooleanProperty.create("north_east");
    public static final BooleanProperty EAST_SOUTH = BooleanProperty.create("east_south");
    public static final BooleanProperty SOUTH_WEST = BooleanProperty.create("south_west");

    public static final BooleanProperty SEPARATOR_NORTH = BooleanProperty.create("separator_north");
    public static final BooleanProperty SEPARATOR_SOUTH = BooleanProperty.create("separator_south");
    public static final BooleanProperty SEPARATOR_EAST = BooleanProperty.create("separator_east");
    public static final BooleanProperty SEPARATOR_WEST = BooleanProperty.create("separator_west");

    private final VoxelShape[] shapesByIndex = makeShapes();
    private final Object2IntMap<BlockState> stateToIndex = new Object2IntOpenHashMap<>();

    public HotpotBlock() {
        super(Properties.of(Material.METAL)
                .noOcclusion()
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops()
                .lightLevel((blockState) -> 15)
                .strength(3.0F, 6.0F));

        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
                .setValue(WEST_NORTH, false)
                .setValue(NORTH_EAST, false)
                .setValue(EAST_SOUTH, false)
                .setValue(SOUTH_WEST, false)
                .setValue(SEPARATOR_NORTH, false)
                .setValue(SEPARATOR_SOUTH, false)
                .setValue(SEPARATOR_EAST, false)
                .setValue(SEPARATOR_WEST, false)
        );
    }

    private VoxelShape[] makeShapes() {
        VoxelShape base = box(0, 0, 0, 16, 8, 16);
        VoxelShape south = box(0, 8, 15, 16, 16, 16); //south(2^0)
        VoxelShape west = box(0, 8, 0, 1, 16, 16); //west(2^1)
        VoxelShape north = box(0, 8, 0, 16, 16, 1); //north(2^2)
        VoxelShape east = box(15, 8, 0, 16, 16, 16); //east(2^3)

        VoxelShape[] faces = {
                VoxelShapes.empty(), //0000 (0)
                south, //0001 (1)
                west, //0010 (2)
                VoxelShapes.or(south, west), //0011 (3)
                north, //0100 (4)
                VoxelShapes.or(north, south), //0101 (5)
                VoxelShapes.or(north, west), //0110 (6)
                VoxelShapes.or(north, west, south), //0111 (7)
                east, //1000 (8)
                VoxelShapes.or(east, south), //1001 (9)
                VoxelShapes.or(east, west), //1010 (10)
                VoxelShapes.or(east, west, south), //1011 (11)
                VoxelShapes.or(east, north), //1100 (12)
                VoxelShapes.or(east, north, south), //1101 (13)
                VoxelShapes.or(east, north, west), //1110 (14)
                VoxelShapes.or(east, north, west, south) //1111 (15)
        };

        for (int i = 0; i < faces.length; i ++) {
            faces[i] = VoxelShapes.or(base, faces[i]);
        }

        return faces;
    }

    private BlockState updateState(BlockState state, BlockPos pos, IWorld accessor) {
        if (!(accessor instanceof World)) {
            return defaultBlockState();
        }

        BlockPosWithLevel selfPos = new BlockPosWithLevel((World) accessor, pos);

        BlockPosWithLevel north = selfPos.north();
        BlockPosWithLevel south = selfPos.south();
        BlockPosWithLevel east = selfPos.east();
        BlockPosWithLevel west = selfPos.west();

        BlockPosWithLevel westNorth = north.west();
        BlockPosWithLevel northEast = east.north();
        BlockPosWithLevel eastSouth = south.east();
        BlockPosWithLevel southWest = west.south();

        boolean northValue = north.is(HotpotModEntry.HOTPOT_BLOCK.get());
        boolean southValue = south.is(HotpotModEntry.HOTPOT_BLOCK.get());
        boolean eastValue = east.is(HotpotModEntry.HOTPOT_BLOCK.get());
        boolean westValue = west.is(HotpotModEntry.HOTPOT_BLOCK.get());

        return state
                .setValue(NORTH, northValue)
                .setValue(SOUTH, southValue)
                .setValue(EAST, eastValue)
                .setValue(WEST, westValue)
                .setValue(WEST_NORTH, westValue && northValue && westNorth.is(HotpotModEntry.HOTPOT_BLOCK.get()))
                .setValue(NORTH_EAST, northValue && eastValue && northEast.is(HotpotModEntry.HOTPOT_BLOCK.get()))
                .setValue(EAST_SOUTH, eastValue && southValue && eastSouth.is(HotpotModEntry.HOTPOT_BLOCK.get()))
                .setValue(SOUTH_WEST, southValue && westValue && southWest.is(HotpotModEntry.HOTPOT_BLOCK.get()))
                .setValue(SEPARATOR_NORTH, northValue && !HotpotBlockEntity.isSameSoup(selfPos, north))
                .setValue(SEPARATOR_SOUTH, southValue && !HotpotBlockEntity.isSameSoup(selfPos, south))
                .setValue(SEPARATOR_EAST, eastValue && !HotpotBlockEntity.isSameSoup(selfPos, east))
                .setValue(SEPARATOR_WEST, westValue && !HotpotBlockEntity.isSameSoup(selfPos, west));
    }

    private int getHitSection(BlockRayTraceResult result) {
        BlockPos blockPos = result.getBlockPos().relative(Direction.UP);

        return HotpotBlockEntity.getPosSection(blockPos, result.getLocation());
    }

    private int getShapeIndex(BlockState state) {
        return stateToIndex.computeIntIfAbsent(state, blockState -> {
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

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        BlockPosWithLevel levelPos = new BlockPosWithLevel(level, pos);

        if (levelPos.getBlockEntity() instanceof HotpotBlockEntity) {
            ItemStack itemStack = player.getItemInHand(hand);
            int hitSection = getHitSection(result);

            if (levelPos.isServerSide()) {
                ((HotpotBlockEntity) levelPos.getBlockEntity()).interact(hitSection, player, hand, itemStack, levelPos);
            }

            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean b) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof HotpotBlockEntity) {
            ((HotpotBlockEntity) level.getBlockEntity(pos)).onRemove(new BlockPosWithLevel(level, pos));
        }

        super.onRemove(state, level, pos, newState, b);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void entityInside(BlockState state, World level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);

        BlockPosWithLevel levelPos = new BlockPosWithLevel(level, pos);

        if (levelPos.getBlockEntity() instanceof HotpotBlockEntity && levelPos.isServerSide()) {
            HotpotBlockEntity hotpotBlockEntity = (HotpotBlockEntity) levelPos.getBlockEntity();
            hotpotBlockEntity.getSoup().entityInside(hotpotBlockEntity, levelPos, entity);
        }
    }

    @Override
    public void animateTick(BlockState blockState, World level, BlockPos pos, Random randomSource) {
        BlockPosWithLevel levelPos = new BlockPosWithLevel(level, pos);

        if (levelPos.getBlockEntity() instanceof HotpotBlockEntity) {
            HotpotBlockEntity hotpotBlockEntity = (HotpotBlockEntity) levelPos.getBlockEntity();
            hotpotBlockEntity.getSoup().animateTick(hotpotBlockEntity, levelPos, randomSource);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction direction, BlockState nearbyState, IWorld accessor, BlockPos pos, BlockPos nearbyPos) {
        return updateState(state, pos, accessor);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, IBlockReader pLevel, BlockPos pPos, ISelectionContext pContext) {
        return shapesByIndex[getShapeIndex(state)];
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return updateState(super.getStateForPlacement(context), context.getClickedPos(), context.getLevel());
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, WEST_NORTH, NORTH_EAST, EAST_SOUTH, SOUTH_WEST, SEPARATOR_NORTH, SEPARATOR_SOUTH, SEPARATOR_EAST, SEPARATOR_WEST);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new HotpotBlockEntity();
    }
}
