package com.github.argon4w.hotpot.placeables;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.blocks.HotpotPlaceableBlockEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

import java.util.ArrayList;
import java.util.List;

public class HotpotEmptyPlaceable implements IHotpotPlaceable {
    @Override
    public IHotpotPlaceable load(CompoundNBT CompoundNBT) {
        return this;
    }

    @Override
    public CompoundNBT save(CompoundNBT CompoundNBT) {
        return CompoundNBT;
    }

    @Override
    public boolean isValid(CompoundNBT CompoundNBT) {
        return true;
    }

    @Override
    public String getID() {
        return "Empty";
    }

    @Override
    public void interact(PlayerEntity player, Hand hand, ItemStack itemStack, int pos, HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel selfPos) {

    }

    @Override
    public ItemStack takeOutContent(int pos, HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel selfPos) {
        return ItemStack.EMPTY;
    }

    @Override
    public void onRemove(HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel pos) {

    }

    @Override
    public void render(TileEntityRendererDispatcher context, HotpotPlaceableBlockEntity hotpotBlockEntity, float partialTick, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int combinedLight, int combinedOverlay) {

    }

    @Override
    public ItemStack getCloneItemStack(HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel level) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean tryPlace(int pos, Direction direction) {
        return false;
    }

    @Override
    public List<Integer> getPos() {
        return new ArrayList<>();
    }

    @Override
    public int getAnchorPos() {
        return 0;
    }

    @Override
    public boolean isConflict(int pos) {
        return false;
    }
}
