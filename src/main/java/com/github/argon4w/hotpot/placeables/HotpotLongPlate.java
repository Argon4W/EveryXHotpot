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

public class HotpotLongPlate implements IHotpotPlaceable {
    private int slot1, slot2;
    private final SimpleItemSlot itemSlot1 = new SimpleItemSlot(), itemSlot2 = new SimpleItemSlot();
    private Direction direction;

    @Override
    public void load(CompoundTag compoundTag) {
        slot1 = compoundTag.getByte("Slot1");
        slot2 = compoundTag.getByte("Slot2");
        direction = HotpotDefinitions.SLOT_TO_DIRECTION.get(slot2 - slot1);

        itemSlot1.load(compoundTag.getCompound("ItemSlot1"));
        itemSlot1.load(compoundTag.getCompound("ItemSlot2"));
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.putByte("Slot1", (byte) slot1);
        compoundTag.putByte("Slot2", (byte) slot2);

        compoundTag.put("ItemSlot1", itemSlot1.save(new CompoundTag()));
        compoundTag.put("ItemSlot2", itemSlot1.save(new CompoundTag()));

        return compoundTag;
    }

    @Override
    public boolean isValid(CompoundTag compoundTag) {
        return compoundTag.contains("Slot1", Tag.TAG_BYTE) && compoundTag.contains("Slot2", Tag.TAG_BYTE) && compoundTag.contains("ItemSlot1", Tag.TAG_COMPOUND) && compoundTag.contains("ItemSlot2", Tag.TAG_COMPOUND);
    }

    @Override
    public String getID() {
        return "Long";
    }

    @Override
    public boolean placeContent(ItemStack itemStack, int slot, HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel pos) {
        return itemSlot1.addItem(itemStack) || itemSlot2.addItem(itemStack);
    }

    @Override
    public ItemStack takeContent(int slot, HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel pos) {
        return slot == slot1 ? itemSlot1.takeItem() : itemSlot2.takeItem();
    }

    @Override
    public void dropAllContent(HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel pos) {
        itemSlot1.dropItem(pos);
        itemSlot2.dropItem(pos);
    }

    @Override
    public void render(BlockEntityRendererProvider.Context context, HotpotPlaceableBlockEntity hotpotBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        float x1 = IHotpotPlaceable.getSlotX(slot1) + 0.25f;
        float z1 = IHotpotPlaceable.getSlotZ(slot1) + 0.25f;

        float x2 = IHotpotPlaceable.getSlotX(slot2) + 0.25f;
        float z2 = IHotpotPlaceable.getSlotZ(slot2) + 0.25f;

        poseStack.pushPose();
        poseStack.translate((x1 + x2) / 2, 0f, (z1 + z2) / 2);
        poseStack.mulPose(Axis.YP.rotationDegrees(direction.toYRot()));
        poseStack.scale(0.8f, 0.8f, 0.8f);

        BakedModel model = context.getBlockRenderDispatcher().getBlockModelShaper().getModelManager().getModel(new ResourceLocation(HotpotModEntry.MODID, "block/hotpot_plate_long"));
        context.getBlockRenderDispatcher().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.solid()), null, model, 1, 1, 1, combinedLight, combinedOverlay, ModelData.EMPTY, RenderType.solid());

        poseStack.popPose();

        int i = 0;

        for (int k = 0; k < itemSlot1.getRenderCount(); k ++, i ++) {
            renderLargePlateItem(context, poseStack, bufferSource, combinedLight, combinedOverlay, itemSlot1, x1, z1, i);
        }

        for (int k = 0; k < itemSlot2.getRenderCount(); k ++, i ++) {
            renderLargePlateItem(context, poseStack, bufferSource, combinedLight, combinedOverlay, itemSlot2, x1, z1, i);
        }
    }

    @Override
    public ItemStack getCloneItemStack(HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel level) {
        return new ItemStack(HotpotModEntry.HOTPOT_LONG_PLATE_BLOCK_ITEM.get());
    }

    public void renderLargePlateItem(BlockEntityRendererProvider.Context context, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, SimpleItemSlot slot, float x, float z, int index) {
        poseStack.pushPose();
        poseStack.translate(x, 0.12f, z);
        poseStack.mulPose(Axis.YN.rotationDegrees(direction.toYRot()));
        poseStack.translate(0f, 0f, -0.14f + index * 0.09f);
        poseStack.mulPose(Axis.XN.rotationDegrees(75));
        poseStack.scale(0.35f, 0.35f, 0.35f);

        slot.renderSlot(context, poseStack, bufferSource, combinedLight, combinedOverlay);

        poseStack.popPose();
    }

    @Override
    public boolean tryPlace(int slot1, Direction direction) {

        int slot2 = slot1 + HotpotDefinitions.DIRECTION_TO_SLOT.get(direction);
        if (isValidSlots(slot1, slot2)) {
            this.slot1 = slot1;
            this.slot2 = slot2;
            this.direction = direction;

            return true;
        }

        return false;
    }

    public boolean isValidSlots(int slot1, int slot2) {
        return 0 <= slot1 && slot1 <= 3 && 0 <= slot2 && slot2 <= 3 && slot1 + slot2 != 3;
    }

    @Override
    public List<Integer> getSlots() {
        return List.of(slot1, slot2);
    }

    @Override
    public boolean isConflict(int slot) {
        return slot1 == slot || slot2 == slot;
    }
}
