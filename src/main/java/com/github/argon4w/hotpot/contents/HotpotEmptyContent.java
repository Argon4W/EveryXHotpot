package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class HotpotEmptyContent implements IHotpotContent {
    public HotpotEmptyContent() {}

    @Override
    public void placed(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {

    }

    @Override
    public void render(TileEntityRendererDispatcher context, HotpotBlockEntity hotpotBlockEntity, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int combinedLight, int combinedOverlay, float offset, float waterline) {}

    @Override
    public ItemStack takeOut(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return ItemStack.EMPTY;
    }

    @Override
    public void onOtherContentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {

    }

    @Override
    public boolean tick(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return false;
    }

    @Override
    public IHotpotContent load(CompoundNBT compoundTag) {
        return this;
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundTag) {
        return compoundTag;
    }

    @Override
    public boolean isValid(CompoundNBT compoundTag) {
        return true;
    }

    @Override
    public String getID() {
        return "Empty";
    }

    @Override
    public String toString() {
        return getID();
    }
}
