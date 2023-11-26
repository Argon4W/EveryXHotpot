package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.IHotpotSavableWIthSlot;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.function.BiConsumer;

public interface IHotpotContent extends IHotpotSavableWIthSlot<IHotpotContent> {
    Map<String, String> ID_FIXES = Map.of(
            "ItemStack", "campfire_recipe_content",
            "BlastingItemStack", "blasting_recipe_content",
            "Player", "player_content",
            "Empty", "empty_content"
    );

    void placed(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    void render(BlockEntityRendererProvider.Context context, HotpotBlockEntity hotpotBlockEntity, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float offset, float waterline);
    boolean tick(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    ItemStack takeOut(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    void onOtherContentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);

    static void loadAll(ListTag listTag, NonNullList<IHotpotContent> list) {
        IHotpotSavableWIthSlot.loadAll(listTag, list.size(), compoundTag -> load(compoundTag, list::set));
    }

    static void load(CompoundTag compoundTag, BiConsumer<Integer, IHotpotContent> consumer) {
        IHotpotContent content = HotpotContents.getContentRegistry().getValue(new ResourceLocation(HotpotModEntry.MODID, fixID(compoundTag.getString("Type")))).createContent();
        consumer.accept(compoundTag.getByte("Slot") & 255, content.loadOrElseGet(compoundTag, () -> HotpotContents.getEmptyContent().createContent()));
    }

    static ListTag saveAll(NonNullList<IHotpotContent> list) {
        return IHotpotSavableWIthSlot.saveAll(list);
    }

    static String fixID(String id) {
        return ID_FIXES.getOrDefault(id, id);
    }
}
