package com.github.argon4w.hotpot.placeables;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.IHotpotSavableWIthSlot;
import com.github.argon4w.hotpot.blocks.HotpotPlaceableBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public interface IHotpotPlaceable extends IHotpotSavableWIthSlot<IHotpotPlaceable> {
    void interact(Player player, InteractionHand hand, ItemStack itemStack, int pos, HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel selfPos);
    ItemStack takeOutContent(int pos, HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel selfPos);
    void onRemove(HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel pos);
    void render(BlockEntityRendererProvider.Context context, HotpotPlaceableBlockEntity hotpotBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay);
    ItemStack getCloneItemStack(HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel level);
    boolean tryPlace(int pos, Direction direction);
    List<Integer> getPos();
    int getAnchorPos();
    boolean isConflict(int pos);

    static float getSlotX(int slot) {
        return ((2 & slot) > 0 ? 0.5f : 0f);
    }

    static float getSlotZ(int slot) {
        return ((1 & slot) > 0 ? 0.5f : 0f);
    }

    static void loadAll(ListTag listTag, NonNullList<IHotpotPlaceable> list) {
        IHotpotSavableWIthSlot.loadAll(listTag, list.size(), compoundTag -> load(compoundTag, (slot, placeable) -> {
            if (placeable instanceof HotpotEmptyPlaceable || Objects.equals(slot, placeable.getAnchorPos())) {
                list.set(slot, placeable);
            }
        }));
    }

    static void load(CompoundTag compoundTag, BiConsumer<Integer, IHotpotPlaceable> consumer) {
        IHotpotPlaceable placeable = HotpotPlaceables.getPlaceableOrElseEmpty(compoundTag.getString("Type")).get();
        consumer.accept(compoundTag.getByte("Slot") & 255, placeable.loadOrElseGet(compoundTag, HotpotPlaceables.getEmptyPlaceable()));
    }

    static ListTag saveAll(NonNullList<IHotpotPlaceable> list) {
        return IHotpotSavableWIthSlot.saveAll(list);
    }
}
