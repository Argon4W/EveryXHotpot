package com.github.argon4w.hotpot.placeables;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.IHotpotSavableWIthSlot;
import com.github.argon4w.hotpot.blocks.HotpotPlaceableBlockEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public interface IHotpotPlaceable extends IHotpotSavableWIthSlot<IHotpotPlaceable> {
    void interact(PlayerEntity player, Hand hand, ItemStack itemStack, int pos, HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel selfPos);
    ItemStack takeOutContent(int pos, HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel selfPos);
    void onRemove(HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel pos);
    void render(TileEntityRendererDispatcher context, HotpotPlaceableBlockEntity hotpotBlockEntity, float partialTick, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int combinedLight, int combinedOverlay);
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

    static void loadAll(ListNBT listTag, NonNullList<IHotpotPlaceable> list) {
        IHotpotSavableWIthSlot.loadAll(listTag, list.size(), compoundTag -> load(compoundTag, (slot, placeable) -> {
            if (placeable instanceof HotpotEmptyPlaceable || Objects.equals(slot, placeable.getAnchorPos())) {
                list.set(slot, placeable);
            }
        }));
    }

    static void load(CompoundNBT compoundTag, BiConsumer<Integer, IHotpotPlaceable> consumer) {
        IHotpotPlaceable placeable = HotpotPlaceables.getPlaceableOrElseEmpty(compoundTag.getString("Type")).get();
        consumer.accept(compoundTag.getByte("Slot") & 255, placeable.loadOrElseGet(compoundTag, HotpotPlaceables.getEmptyPlaceable()));
    }

    static ListNBT saveAll(NonNullList<IHotpotPlaceable> list) {
        return IHotpotSavableWIthSlot.saveAll(list);
    }
}
