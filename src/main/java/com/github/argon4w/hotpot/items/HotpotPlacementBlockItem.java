package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainerBlockEntity;
import com.github.argon4w.hotpot.placements.IHotpotPlacement;
import com.github.argon4w.hotpot.placements.IHotpotPlacementSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
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

    public void loadPlacement(IHotpotPlacementContainerBlockEntity container, LevelBlockPos pos, T placement, ItemStack itemStack) {

    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        LevelBlockPos selfPos = LevelBlockPos.fromUseOnContext(context);
        Direction direction = context.getHorizontalDirection();
        int pos = getHitPos(context);
        int layer = getLayer(context);

        IHotpotPlacementSerializer<T> serializer = holder.value();
        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();
        ItemStack itemStack = context.getItemInHand();

        if (!(selfPos.getBlockEntity() instanceof IHotpotPlacementContainerBlockEntity)) {
            selfPos = LevelBlockPos.fromBlockPlaceContext(new BlockPlaceContext(context));
        }

        if (!canPlace(context.getPlayer(), context.getHand(), selfPos)) {
            return InteractionResult.PASS;
        }

        if (!(selfPos.getBlockEntity() instanceof IHotpotPlacementContainerBlockEntity container)) {
            return super.useOn(context);
        }

        if (!serializer.canPlace(pos, direction)) {
            return super.useOn(context);
        }

        if (!place(selfPos, serializer.get(pos, direction), itemStack.copy(), pos, layer)) {
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
        Direction direction = context.getHorizontalDirection();

        int pos = getHitPos(context);
        int layer = getLayer(context);

        IHotpotPlacementSerializer<T> factory = holder.value();

        if (!factory.canPlace(pos, direction)) {
            return InteractionResult.FAIL;
        }

        InteractionResult result = super.place(context);
        place(selfPos, factory.get(pos, direction), itemStack, pos, layer);

        return result;
    }

    public boolean place(LevelBlockPos selfPos, T placement, ItemStack itemStack, int pos, int layer) {
        if (!selfPos.isServerSide()) {
            return false;
        }

        if (!(selfPos.getBlockEntity() instanceof IHotpotPlacementContainerBlockEntity container)) {
            return false;
        }

        if (!container.place(placement, pos, layer)) {
            return false;
        }

        loadPlacement(container, selfPos, placement, itemStack);
        return true;
    }

    public void playSound(LevelBlockPos pos, Player player) {
        SoundType soundtype = pos.getSoundType(player);
        SoundEvent soundEvent = this.getPlaceSound(pos.getBlockState(), pos.level(), pos.pos(), player);
        float volume = (soundtype.getVolume() + 1.0F) / 2.0F;
        float pitch = soundtype.getPitch() * 0.8F;

        pos.playSound(soundEvent, volume, pitch);
    }

    public static int getLayer(BlockPos pos , Vec3 location) {
        Vec3 vec = location.subtract(pos.getX(), pos.getY(), pos.getZ());
        return vec.y() < 0.5 ? 0 : 1;
    }

    public static int getHitPos(BlockPos pos, Vec3 location) {
        Vec3 vec = location.subtract(pos.getX(), pos.getY(), pos.getZ());
        return (vec.z() < 0.5 ? 0 : 1) | (vec.x() < 0.5 ? 0 : 2);
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
}
