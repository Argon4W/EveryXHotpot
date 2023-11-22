package com.github.argon4w.hotpot.placeables;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.blocks.HotpotPlaceableBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
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
    public void interact(Player player, InteractionHand hand, ItemStack itemStack, int pos, HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel selfPos) {

    }

    @Override
    public ItemStack takeOutContent(int pos, HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel selfPos) {
        return ItemStack.EMPTY;
    }

    @Override
    public void onRemove(HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel pos) {

    }

    @Override
    public void render(BlockEntityRendererProvider.Context context, HotpotPlaceableBlockEntity hotpotBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {

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
        return List.of();
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
