package com.github.argon4w.hotpot.placeables;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class SimpleItemSlot {
    private ItemStack itemSlot = ItemStack.EMPTY;


    public SimpleItemSlot() {

    }

    public void renderSlot(BlockEntityRendererProvider.Context context, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        context.getItemRenderer().renderStatic(itemSlot, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource, ItemTransforms.TransformType.GROUND.ordinal());
    }

    public int getRenderCount() {
        return itemSlot.isEmpty() ? 0 : Math.max(1, itemSlot.getCount() / 16);
    }

    public boolean addItem(ItemStack itemStack) {
        if (itemSlot.isEmpty()) {
            itemSlot = itemStack.copy();
            itemStack.setCount(0);

            return true;
        } else if (ItemStack.isSameItemSameTags(itemStack, itemSlot)) {
            moveItemWithCount(itemStack);

            return itemStack.isEmpty();
        }

        return false;
    }

    private void moveItemWithCount(ItemStack itemStack) {
        int j = Math.min(itemStack.getCount(), itemSlot.getMaxStackSize() - itemSlot.getCount());
        if (j > 0) {
            itemSlot.grow(j);
            itemStack.shrink(j);
        }
    }

    public ItemStack takeItem(boolean consume) {
        return consume ?  itemSlot.split(1) : itemSlot.copyWithCount(1);
    }

    public boolean isEmpty() {
        return itemSlot.isEmpty();
    }

    public void dropItem(BlockPosWithLevel pos) {
        ItemStack itemStack = itemSlot.copy();
        itemSlot.setCount(0);

        pos.dropItemStack(itemStack);
    }

    public CompoundTag save(CompoundTag compoundTag) {
        itemSlot.save(compoundTag);

        return compoundTag;
    }

    public void load(CompoundTag compoundTag) {
        itemSlot = ItemStack.of(compoundTag);
    }
}
