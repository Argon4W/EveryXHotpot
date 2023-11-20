package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.IHotpotSavableWIthSlot;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;

import java.util.function.BiConsumer;

public interface IHotpotContent extends IHotpotSavableWIthSlot<IHotpotContent> {
    void placed(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    void render(TileEntityRendererDispatcher context, HotpotBlockEntity hotpotBlockEntity, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int combinedLight, int combinedOverlay, float offset, float waterline);
    boolean tick(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    ItemStack takeOut(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    void onOtherContentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);

    static void loadAll(ListNBT listTag, NonNullList<IHotpotContent> list) {
        IHotpotSavableWIthSlot.loadAll(listTag, list.size(), compoundTag -> load(compoundTag, list::set));
    }

    static void load(CompoundNBT compoundTag, BiConsumer<Integer, IHotpotContent> consumer) {
        IHotpotContent content = HotpotContents.getContentOrElseEmpty(compoundTag.getString("Type")).get();
        consumer.accept(compoundTag.getByte("Slot") & 255, content.loadOrElseGet(compoundTag, HotpotContents.getEmptyContent()));
    }

    static ListNBT saveAll(NonNullList<IHotpotContent> list) {
        return IHotpotSavableWIthSlot.saveAll(list);
    }
}
