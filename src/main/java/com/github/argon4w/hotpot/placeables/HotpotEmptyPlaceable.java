package com.github.argon4w.hotpot.plates;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.blocks.HotpotPlaceableBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class HotpotEmptyPlaceable implements IHotpotPlaceable {
    @Override
    public IHotpotPlaceable load(CompoundTag compoundTag) {
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
        return "Empty";
    }

    @Override
    public boolean placeContent(ItemStack itemStack, int slot, HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel pos) {
        return false;
    }

    @Override
    public ItemStack takeContent(int slot, HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel pos) {
        return ItemStack.EMPTY;
    }

    @Override
    public void dropAllContent(HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel pos) {

    }

    @Override
    public void render(BlockEntityRendererProvider.Context context, HotpotPlaceableBlockEntity hotpotBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {

    }

    @Override
    public ItemStack getCloneItemStack(HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel level) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean tryPlace(int slot, Direction direction) {
        return false;
    }

    @Override
    public List<Integer> getSlots() {
        return List.of();
    }

    @Override
    public boolean isConflict(int slot) {
        return false;
    }
}
