package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainer;
import com.github.argon4w.hotpot.placements.coords.ComplexDirection;
import com.github.argon4w.hotpot.placements.coords.HotpotPlacementCoords;
import com.github.argon4w.hotpot.placements.coords.HotpotPlacementPositions;
import com.github.argon4w.hotpot.placements.IHotpotPlacement;
import com.github.argon4w.hotpot.placements.IHotpotPlacementSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class HotpotPlacementBlockItem<T extends IHotpotPlacement> extends BlockItem {
    private final DeferredHolder<IHotpotPlacementSerializer<?>, ? extends IHotpotPlacementSerializer<T>> holder;

    public HotpotPlacementBlockItem(DeferredHolder<IHotpotPlacementSerializer<?>, ? extends IHotpotPlacementSerializer<T>> holder) {
        super(HotpotModEntry.HOTPOT_PLACEMENT.get(), new Properties().stacksTo(64));
        this.holder = holder;
    }

    public HotpotPlacementBlockItem(DeferredHolder<IHotpotPlacementSerializer<?>, ? extends IHotpotPlacementSerializer<T>> holder, Properties properties) {
        super(HotpotModEntry.HOTPOT_PLACEMENT.get(), properties);
        this.holder = holder;
    }

    public boolean canPlace(Player player, InteractionHand hand, LevelBlockPos pos) {
        return true;
    }

    public void loadPlacement(IHotpotPlacementContainer container, LevelBlockPos pos, T placement, ItemStack itemStack) {

    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        LevelBlockPos selfPos = LevelBlockPos.fromUseOnContext(context);
        ComplexDirection direction = ComplexDirection.fromDirection(context.getHorizontalDirection());
        int pos = getHitPos(context);
        int layer = getLayer(context);

        IHotpotPlacementSerializer<T> serializer = holder.value();
        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();
        ItemStack itemStack = context.getItemInHand();

        if (selfPos.getBlockEntity() instanceof IHotpotPlacementContainer placementContainer) {
            layer += placementContainer.getLayerOffset();
        } else {
            selfPos = LevelBlockPos.fromBlockPlaceContext(new BlockPlaceContext(context));
        }

        if (!canPlace(context.getPlayer(), context.getHand(), selfPos)) {
            return InteractionResult.PASS;
        }

        if (!(selfPos.getBlockEntity() instanceof IHotpotPlacementContainer container)) {
            return super.useOn(context);
        }

        if (!place(selfPos, serializer, direction, itemStack.copy(), pos, layer)) {
            container.interact(pos, layer, player, hand, itemStack, selfPos);
            return InteractionResult.SUCCESS_NO_ITEM_USED;
        }

        playSound(selfPos, context.getPlayer());

        if (player == null || !player.getAbilities().instabuild) {
            itemStack.shrink(1);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    @NotNull
    @Override
    public InteractionResult place(BlockPlaceContext context) {
        LevelBlockPos selfPos = LevelBlockPos.fromBlockPlaceContext(context);
        ItemStack itemStack = context.getItemInHand().copy();
        ComplexDirection direction = ComplexDirection.fromDirection(context.getHorizontalDirection());

        int pos = getHitPos(context);
        int layer = getLayer(context);

        IHotpotPlacementSerializer<T> serializer = holder.value();
        List<Optional<Integer>> positions = serializer.getPositions(pos, direction);

        if (positions.isEmpty()) {
            return InteractionResult.FAIL;
        }

        List<Integer> occupiedPositions = ComplexDirection.getNearbyOccupiedPositions(selfPos, layer);
        List<Integer> nonConflictPositions = isNotConflict(positions, layer, selfPos, occupiedPositions);

        if (nonConflictPositions.size() != positions.size()) {
            return InteractionResult.FAIL;
        }

        InteractionResult result = super.place(context);
        place(selfPos, serializer, nonConflictPositions, direction, itemStack, pos, layer);

        return result;
    }

    public boolean place(LevelBlockPos selfPos, IHotpotPlacementSerializer<T> serializer, ComplexDirection direction, ItemStack itemStack, int pos, int layer) {
        List<Integer> occupiedPositions = ComplexDirection.getNearbyOccupiedPositions(selfPos, layer);
        List<Optional<Integer>> positions = serializer.getPositions(pos, direction);

        if (positions.isEmpty()) {
            return false;
        }

        List<Integer> nonConflictPositions = isNotConflict(positions, layer, selfPos, occupiedPositions);

        if (nonConflictPositions.size() != positions.size()) {
            return false;
        }

        return place(selfPos, serializer, nonConflictPositions, direction, itemStack, pos, layer);
    }

    public boolean place(LevelBlockPos selfPos, IHotpotPlacementSerializer<T> serializer, List<Integer> positions, ComplexDirection direction, ItemStack itemStack, int pos, int layer) {
        if (!selfPos.isServerSide()) {
            return false;
        }

        if (!(selfPos.getBlockEntity() instanceof IHotpotPlacementContainer container)) {
            return false;
        }

        T placement = serializer.get(positions, direction);
        container.place(placement, pos, layer);

        loadPlacement(container, selfPos, placement, itemStack);
        return true;
    }

    public void playSound(LevelBlockPos pos, Player player) {
        SoundType soundtype = pos.getSoundType(player);
        pos.playSound(this.getPlaceSound(pos.getBlockState(), pos.level(), pos.pos(), player), (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
    }

    public static int getLayer(BlockPos pos , Vec3 location) {
        Vec3 vec = location.subtract(pos.getX(), pos.getY(), pos.getZ());
        return vec.y() < 0.5 ? 0 : 1;
    }

    public static int getHitPos(BlockPos pos, Vec3 location) {
        return HotpotPlacementPositions.getClickPosition(pos, location);
    }

    public static int getLayer(BlockHitResult result) {
        return getLayer(result.getBlockPos(), result.getLocation());
    }

    public static int getLayer(BlockPlaceContext context) {
        return getLayer(context.getClickedPos(), context.getClickLocation());
    }

    public static int getLayer(UseOnContext context) {
        return getLayer(context.getClickedPos(), context.getClickLocation());
    }

    public static int getHitPos(BlockHitResult result) {
        return getHitPos(result.getBlockPos(), result.getLocation());
    }

    public static int getHitPos(BlockPlaceContext context) {
        return getHitPos(context.getClickedPos(), context.getClickLocation());
    }

    public static int getHitPos(UseOnContext context) {
        return getHitPos(context.getClickedPos(), context.getClickLocation());
    }

    public static List<Integer> isNotConflict(List<Optional<Integer>> positions, int layer, LevelBlockPos pos, List<Integer> occupiedPositions) {
        return positions.stream().filter(Optional::isPresent).map(Optional::get).filter(i -> !occupiedPositions.contains(i) && isPositionNotConflict(i, layer, pos)).toList();
    }

    public static boolean isPositionNotConflict(int position, int layer, LevelBlockPos pos) {
        return pos.getBlockState().isAir() || pos.getBlockEntity() instanceof IHotpotPlacementContainer blockEntity && blockEntity.isPositionValid(position, layer);
    }
}
