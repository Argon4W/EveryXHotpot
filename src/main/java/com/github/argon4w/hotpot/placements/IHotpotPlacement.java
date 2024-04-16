package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.IHotpotSavableWIthSlot;
import com.github.argon4w.hotpot.blocks.HotpotPlacementBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.BiConsumer;

public interface IHotpotPlacement extends IHotpotSavableWIthSlot<IHotpotPlacement> {
    boolean interact(Player player, InteractionHand hand, ItemStack itemStack, int pos, HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos selfPos);
    ItemStack takeOutContent(int pos, HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos selfPos);
    void onRemove(HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos pos);
    ItemStack getCloneItemStack(HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos level);
    boolean canPlace(int pos, Direction direction);
    List<Integer> getPos();
    boolean isConflict(int pos);

    static float getSlotX(int slot) {
        return ((2 & slot) > 0 ? 0.5f : 0f);
    }

    static float getSlotZ(int slot) {
        return ((1 & slot) > 0 ? 0.5f : 0f);
    }

    static void loadAll(ListTag listTag, NonNullList<IHotpotPlacement> list) {
        IHotpotSavableWIthSlot.loadAll(listTag, list.size(), compoundTag -> load(compoundTag, list::set));
    }

    static void load(CompoundTag compoundTag, BiConsumer<Integer, IHotpotPlacement> consumer) {
        IHotpotPlacement placement = HotpotPlacements.getPlacementFactory(new ResourceLocation(compoundTag.getString("Type"))).build();
        consumer.accept(compoundTag.getByte("Slot") & 255, placement.loadOrElseGet(compoundTag, () -> HotpotPlacements.getEmptyPlacement().build()));
    }

    static ListTag saveAll(NonNullList<IHotpotPlacement> list) {
        return IHotpotSavableWIthSlot.saveAll(list);
    }
}
