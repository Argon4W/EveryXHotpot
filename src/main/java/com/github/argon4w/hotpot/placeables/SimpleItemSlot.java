package com.github.argon4w.hotpot.placeables;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class SimpleItemSlot {
    private ItemStack itemSlot = ItemStack.EMPTY;


    public SimpleItemSlot() {

    }

    public void renderSlot(TileEntityRendererDispatcher context, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int combinedLight, int combinedOverlay) {
        Minecraft.getInstance().getItemRenderer().renderStatic(itemSlot, ItemCameraTransforms.TransformType.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource);
    }

    public int getRenderCount() {
        return itemSlot.isEmpty() ? 0 : Math.max(1, itemSlot.getCount() / 16);
    }

    public boolean addItem(ItemStack itemStack) {
        if (itemSlot.isEmpty()) {
            itemSlot = itemStack.copy();
            itemStack.setCount(0);

            return true;
        } else if (ItemStack.matches(itemStack, itemSlot)) {
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
        return consume ?  itemSlot.split(1) : copyWithCount(itemSlot, 1);
    }

    public ItemStack copyWithCount(ItemStack itemStack, int count) {
        ItemStack copied = itemStack.copy();
        copied.setCount(count);

        return copied;
    }

    public boolean isEmpty() {
        return itemSlot.isEmpty();
    }

    public void dropItem(BlockPosWithLevel pos) {
        pos.dropItemStack(itemSlot.copy());
        itemSlot.setCount(0);
    }

    public CompoundNBT save(CompoundNBT compoundTag) {
        itemSlot.save(compoundTag);

        return compoundTag;
    }

    public void load(CompoundNBT compoundTag) {
        itemSlot = ItemStack.of(compoundTag);
    }
}
