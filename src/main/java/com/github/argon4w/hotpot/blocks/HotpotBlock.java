package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HotpotBlock extends BaseEntityBlock implements Equipable {
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
    public static final BooleanProperty HOTPOT_LIT = BooleanProperty.create("hotpot_lit");

    private final VoxelShape[] shapesByIndex = makeShapes();
    private final Object2IntMap<BlockState> stateToIndex = new Object2IntOpenHashMap<>();

    public HotpotBlock() {
        super(Properties.of()
                .forceSolidOn()
                .noOcclusion()
                .mapColor(MapColor.METAL)
                .sound(SoundType.COPPER)
                .requiresCorrectToolForDrops()
                .lightLevel((blockState) -> blockState.getValue(HOTPOT_LIT) ? 15 : 0)
                .isViewBlocking((pState, pLevel, pPos) -> false)
                .strength(3.0F, 6.0F));

        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(HOTPOT_LIT, true)
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

    private BlockState updateState(BlockState state, BlockPos pos, LevelAccessor accessor) {
        if (!(accessor instanceof Level level)) {
            return defaultBlockState();
        }

        LevelBlockPos selfPos = new LevelBlockPos(level, pos);

        boolean hotpotLit = true;

        if (selfPos.getBlockEntity() instanceof HotpotBlockEntity hotpotBlockEntity) {
            hotpotLit = hotpotBlockEntity.getSoup().isHotpotLit(hotpotBlockEntity, selfPos);
        }

        LevelBlockPos north = selfPos.north();
        LevelBlockPos south = selfPos.south();
        LevelBlockPos east = selfPos.east();
        LevelBlockPos west = selfPos.west();

        LevelBlockPos westNorth = north.west();
        LevelBlockPos northEast = east.north();
        LevelBlockPos eastSouth = south.east();
        LevelBlockPos southWest = west.south();

        boolean northValue = north.is(HotpotModEntry.HOTPOT_BLOCK.get());
        boolean southValue = south.is(HotpotModEntry.HOTPOT_BLOCK.get());
        boolean eastValue = east.is(HotpotModEntry.HOTPOT_BLOCK.get());
        boolean westValue = west.is(HotpotModEntry.HOTPOT_BLOCK.get());

        return state
                .setValue(HOTPOT_LIT, hotpotLit)
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

    @SuppressWarnings("deprecation")
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
    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        LevelBlockPos levelPos = new LevelBlockPos(level, pos);

        if (!(levelPos.getBlockEntity() instanceof HotpotBlockEntity hotpotBlockEntity)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        int hitPos = HotpotBlockEntity.getHitPos(hitResult.getBlockPos(), hitResult.getLocation());

        if (levelPos.isServerSide()) {
            hotpotBlockEntity.interact(hitPos, 0, player, hand, itemStack, levelPos);
        }

        return ItemInteractionResult.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean b) {
        if (state.is(newState.getBlock())) {
            return;
        }

        if (level.getBlockEntity(pos) instanceof HotpotBlockEntity hotpotBlockEntity) {
            hotpotBlockEntity.onRemove(new LevelBlockPos(level, pos));
        }

        super.onRemove(state, level, pos, newState, b);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);

        LevelBlockPos levelPos = new LevelBlockPos(level, pos);

        if (!levelPos.isServerSide()) {
            return;
        }

        if (!(levelPos.getBlockEntity() instanceof HotpotBlockEntity hotpotBlockEntity)) {
            return;
        }

        hotpotBlockEntity.getSoup().onEntityInside(hotpotBlockEntity, levelPos, entity);
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos pos, RandomSource randomSource) {
        LevelBlockPos levelPos = new LevelBlockPos(level, pos);

        if (!(levelPos.getBlockEntity() instanceof HotpotBlockEntity hotpotBlockEntity)) {
            return;
        }

        hotpotBlockEntity.getSoup().animateTick(hotpotBlockEntity, levelPos, randomSource);
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
        return level.isClientSide ? null : createTickerHelper(blockEntityType, HotpotModEntry.HOTPOT_BLOCK_ENTITY.get(), HotpotBlockEntity::tick);
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
        builder.add(HOTPOT_LIT, NORTH, SOUTH, EAST, WEST, WEST_NORTH, NORTH_EAST, EAST_SOUTH, SOUTH_WEST, SEPARATOR_NORTH, SEPARATOR_SOUTH, SEPARATOR_EAST, SEPARATOR_WEST);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HotpotBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<HotpotBlock> codec() {
        return MapCodec.unit(HotpotBlock::new);
    }

    @NotNull
    @Override
    public  RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.MODEL;
    }

    @NotNull
    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }
}
