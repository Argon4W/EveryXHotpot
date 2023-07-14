package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotDefinitions;
import com.github.argon4w.hotpot.IHotpotSavable;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.util.BiConsumer;

public interface IHotpotContent extends IHotpotSavable {
    void placed(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    void render(BlockEntityRendererProvider.Context context, HotpotBlockEntity hotpotBlockEntity, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float offset, float waterline);
    boolean tick(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    ItemStack takeOut(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);

    static void loadAllContents(CompoundTag compoundTag, NonNullList<IHotpotContent> contents) {
        if (compoundTag.contains("Contents")) {
            contents.clear();

            compoundTag.getList("Contents", Tag.TAG_COMPOUND).forEach(tag -> loadContent((CompoundTag) tag, (slot, content) -> {
                if (slot < contents.size()) {
                    contents.set(slot, content);
                }
            }));
        }
    }

    static void loadContent(CompoundTag compoundTag, BiConsumer<Integer, IHotpotContent> consumer) {
        if (!isContentTagValid(compoundTag)) {
            return;
        }

        int slot = compoundTag.getByte("Slot") & 255;
        IHotpotContent content = HotpotDefinitions.getContentOrElseEmpty(compoundTag.getString("Type")).get();

        consumer.accept(slot, IHotpotSavable.loadOrElseGet(content, compoundTag, HotpotDefinitions.getEmptyContent()));
    }

    static boolean isContentTagValid(CompoundTag compoundTag) {
        return compoundTag.contains("Slot", Tag.TAG_BYTE) && compoundTag.contains("Type", Tag.TAG_STRING);
    }

    static void saveAllContents(CompoundTag compoundTag, NonNullList<IHotpotContent> contents) {
        ListTag list = new ListTag();

        for(int i = 0; i < contents.size(); ++i) {
            IHotpotContent content = contents.get(i);
            CompoundTag tag = new CompoundTag();

            tag.putString("Type", content.getID());
            tag.putByte("Slot", (byte) i);

            list.add(content.save(tag));
        }

        compoundTag.put("Contents", list);
    }
}
