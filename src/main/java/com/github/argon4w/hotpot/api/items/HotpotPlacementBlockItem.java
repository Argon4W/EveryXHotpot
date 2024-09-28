package com.github.argon4w.hotpot.api.items;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.api.blocks.IHotpotPlacementContainer;
import com.github.argon4w.hotpot.api.placements.IHotpotPlacement;
import com.github.argon4w.hotpot.api.placements.IHotpotPlacementSerializer;
import com.github.argon4w.hotpot.placements.coords.ComplexDirection;
import com.github.argon4w.hotpot.placements.coords.HotpotPlacementCoords;
import com.github.argon4w.hotpot.placements.coords.HotpotPlacementPositions;
import net.minecraft.core.BlockPos;
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
        LevelBlockPos pos = LevelBlockPos.fromUseOnContext(context);
        ComplexDirection direction = ComplexDirection.fromDirection(context.getHorizontalDirection());
        int position = getPosition(context);
        int layer = 0;

        IHotpotPlacementSerializer<T> serializer = holder.value();
        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();
        ItemStack itemStack = context.getItemInHand();

        if (pos.getBlockEntity() instanceof IHotpotPlacementContainer placementContainer) {
            layer += placementContainer.getLayer(getVec3(context));
        }

        if (!(pos.getBlockEntity() instanceof IHotpotPlacementContainer)) {
            pos = LevelBlockPos.fromBlockPlaceContext(new BlockPlaceContext(context));
        }

        if (!canPlace(context.getPlayer(), context.getHand(), pos)) {
            return InteractionResult.PASS;
        }

        if (!(pos.getBlockEntity() instanceof IHotpotPlacementContainer)) {
            return super.useOn(context);
        }

        if (!place(pos, serializer, direction, itemStack.copy(), position, layer)) {
            HotpotPlacementCoords.interactNearbyPositions(pos, player, hand, itemStack, position, layer);
            return InteractionResult.SUCCESS_NO_ITEM_USED;
        }

        playSound(pos, context.getPlayer());

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
        LevelBlockPos pos = LevelBlockPos.fromBlockPlaceContext(context);
        ItemStack itemStack = context.getItemInHand().copy();
        ComplexDirection direction = ComplexDirection.fromDirection(context.getHorizontalDirection());

        int position = getPosition(context);
        int layer = 0;

        IHotpotPlacementSerializer<T> serializer = holder.value();
        List<Optional<Integer>> positions = serializer.getPositions(position, direction);

        if (positions.isEmpty()) {
            return InteractionResult.FAIL;
        }

        List<Integer> occupiedPositions = HotpotPlacementCoords.getNearbyOccupiedPositions(pos, layer);
        List<Integer> nonConflictPositions = isNotConflict(positions, layer, pos, occupiedPositions);

        if (nonConflictPositions.size() != positions.size()) {
            return InteractionResult.FAIL;
        }

        InteractionResult result = super.place(context);
        place(pos, serializer, nonConflictPositions, direction, itemStack, position, layer);

        return result;
    }

    public boolean place(LevelBlockPos pos, IHotpotPlacementSerializer<T> serializer, ComplexDirection direction, ItemStack itemStack, int position, int layer) {
        List<Integer> occupiedPositions = HotpotPlacementCoords.getNearbyOccupiedPositions(pos, layer);
        List<Optional<Integer>> positions = serializer.getPositions(position, direction);

        if (positions.isEmpty()) {
            return false;
        }

        List<Integer> nonConflictPositions = isNotConflict(positions, layer, pos, occupiedPositions);

        if (nonConflictPositions.size() != positions.size()) {
            return false;
        }

        return place(pos, serializer, nonConflictPositions, direction, itemStack, position, layer);
    }

    public boolean place(LevelBlockPos pos, IHotpotPlacementSerializer<T> serializer, List<Integer> positions, ComplexDirection direction, ItemStack itemStack, int position, int layer) {
        if (!pos.isServerSide()) {
            return false;
        }

        if (!(pos.getBlockEntity() instanceof IHotpotPlacementContainer container)) {
            return false;
        }

        T placement = serializer.get(positions, direction);
        container.place(placement, position, layer, pos);

        loadPlacement(container, pos, placement, itemStack);
        return true;
    }

    public void playSound(LevelBlockPos pos, Player player) {
        SoundType soundtype = pos.getSoundType(player);
        pos.playSound(this.getPlaceSound(pos.getBlockState(), pos.level(), pos.pos(), player), (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
    }

    public static int getPosition(BlockPos pos, Vec3 location) {
        return HotpotPlacementPositions.getPosition(pos, location);
    }

    public static Vec3 getVec3(BlockPos pos , Vec3 location) {
        return location.subtract(pos.getX(), pos.getY(), pos.getZ());
    }

    public static Vec3 getVec3(UseOnContext context) {
        return getVec3(context.getClickedPos(), context.getClickLocation());
    }

    public static int getPosition(BlockHitResult result) {
        return getPosition(result.getBlockPos(), result.getLocation());
    }

    public static int getPosition(BlockPlaceContext context) {
        return getPosition(context.getClickedPos(), context.getClickLocation());
    }

    public static int getPosition(UseOnContext context) {
        return getPosition(context.getClickedPos(), context.getClickLocation());
    }

    public static List<Integer> isNotConflict(List<Optional<Integer>> positions, int layer, LevelBlockPos pos, List<Integer> occupiedPositions) {
        return positions.stream().filter(Optional::isPresent).map(Optional::get).filter(i -> !occupiedPositions.contains(i) && isPositionNotConflict(i, layer, pos)).toList();
    }

    public static boolean isPositionNotConflict(int position, int layer, LevelBlockPos pos) {
        return pos.getBlockState().isAir() || pos.getBlockEntity() instanceof IHotpotPlacementContainer blockEntity && blockEntity.getProvidedPositions(layer, pos).contains(position);
    }
}
