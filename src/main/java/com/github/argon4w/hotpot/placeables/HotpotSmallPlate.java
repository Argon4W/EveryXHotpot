package com.github.argon4w.hotpot.plates;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotDefinitions;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotPlaceableBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.data.ModelData;

import java.util.List;

public class HotpotSmallPlate implements IHotpotPlaceable {
    private int slot;
    private int directionSlot;
    private Direction direction;
    private final SimpleItemSlot itemSlot = new SimpleItemSlot();

    @Override
    public void load(CompoundTag compoundTag) {
        slot = compoundTag.getByte("Slot");
        directionSlot = compoundTag.getByte("DirectionSlot");
        direction = HotpotDefinitions.SLOT_TO_DIRECTION.get(directionSlot - slot);

        itemSlot.load(compoundTag.getCompound("ItemSlot"));
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.putByte("Slot", (byte) slot);
        compoundTag.putByte("DirectionSlot", (byte) directionSlot);

        compoundTag.put("ItemSlot", itemSlot.save(new CompoundTag()));

        return compoundTag;
    }

    @Override
    public boolean isValid(CompoundTag compoundTag) {
        return compoundTag.contains("Slot", Tag.TAG_BYTE) && compoundTag.contains("ItemSlot", Tag.TAG_COMPOUND);
    }

    @Override
    public String getID() {
        return "Small";
    }

    @Override
    public boolean placeContent(ItemStack itemStack, int slot, HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel pos) {
        return itemSlot.addItem(itemStack);
    }

    @Override
    public ItemStack takeContent(int slot, HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel pos) {
        return itemSlot.takeItem();
    }

    @Override
    public void dropAllContent(HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel pos) {
        itemSlot.dropItem(pos);
    }

    @Override
    public void render(BlockEntityRendererProvider.Context context, HotpotPlaceableBlockEntity hotpotBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        float x = IHotpotPlaceable.getSlotX(slot) + 0.25f;
        float z = IHotpotPlaceable.getSlotZ(slot) + 0.25f;

        poseStack.pushPose();
        poseStack.translate(x, 0f, z);
        poseStack.scale(0.8f, 0.8f, 0.8f);

        BakedModel model = context.getBlockRenderDispatcher().getBlockModelShaper().getModelManager().getModel(new ResourceLocation(HotpotModEntry.MODID, "block/hotpot_plate_small"));
        context.getBlockRenderDispatcher().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.translucent()), null, model, 1, 1, 1, combinedLight, combinedOverlay, ModelData.EMPTY, RenderType.translucent());

        poseStack.popPose();

        for (int i = 0; i < itemSlot.getRenderCount(); i ++) {
            poseStack.pushPose();

            poseStack.translate(x, 0.065f + 0.02 * i, z);
            poseStack.mulPose(Axis.YP.rotationDegrees(direction.toYRot() + (i % 2) * 20));
            poseStack.mulPose(Axis.XP.rotationDegrees(90f));
            poseStack.scale(0.35f, 0.35f, 0.35f);

            itemSlot.renderSlot(context, poseStack, bufferSource, combinedLight, combinedOverlay);

            poseStack.popPose();
        }
    }

    @Override
    public ItemStack getCloneItemStack(HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel level) {
        return new ItemStack(HotpotModEntry.HOTPOT_SMALL_PLATE_BLOCK_ITEM.get());
    }

    @Override
    public boolean tryPlace(int slot1, Direction direction) {
        this.slot = slot1;
        this.directionSlot = slot1 + HotpotDefinitions.DIRECTION_TO_SLOT.get(direction);
        this.direction = direction;

        return true;
    }

    @Override
    public List<Integer> getSlots() {
        return List.of(slot);
    }

    @Override
    public boolean isConflict(int slot) {
        return this.slot == slot;
    }
}
