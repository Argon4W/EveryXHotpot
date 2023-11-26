package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class HotpotEmptyContent implements IHotpotContent {
    public HotpotEmptyContent() {}

    @Override
    public void placed(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {

    }

    @Override
    public void render(BlockEntityRendererProvider.Context context, HotpotBlockEntity hotpotBlockEntity, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float offset, float waterline) {}

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
    public IHotpotContent load(CompoundTag compoundTag) {
        return this;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        return compoundTag;
    }

    @Override
    public boolean isValid(CompoundTag compoundTag) {
        return true;
    }

    @Override
    public String getID() {
        return "empty_content";
    }

    @Override
    public String toString() {
        return getID();
    }
}
