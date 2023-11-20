package com.github.argon4w.hotpot.placeables;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotPlaceableBlockEntity;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.common.util.Constants;

import java.util.List;

public class HotpotLongPlate implements IHotpotPlaceable {
    private int pos1, pos2;
    private final SimpleItemSlot itemSlot1 = new SimpleItemSlot(), itemSlot2 = new SimpleItemSlot();
    private Direction direction;

    @Override
    public IHotpotPlaceable load(CompoundNBT compoundTag) {
        pos1 = compoundTag.getByte("Pos1");
        pos2 = compoundTag.getByte("Pos2");
        direction = HotpotPlaceables.POS_TO_DIRECTION.get(pos2 - pos1);

        itemSlot1.load(compoundTag.getCompound("ItemSlot1"));
        itemSlot2.load(compoundTag.getCompound("ItemSlot2"));

        return this;
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundTag) {
        compoundTag.putByte("Pos1", (byte) pos1);
        compoundTag.putByte("Pos2", (byte) pos2);

        compoundTag.put("ItemSlot1", itemSlot1.save(new CompoundNBT()));
        compoundTag.put("ItemSlot2", itemSlot2.save(new CompoundNBT()));

        return compoundTag;
    }

    @Override
    public boolean isValid(CompoundNBT compoundTag) {
        return compoundTag.contains("Pos1", Constants.NBT.TAG_BYTE) && compoundTag.contains("Pos2", Constants.NBT.TAG_BYTE) && compoundTag.contains("ItemSlot1", Constants.NBT.TAG_COMPOUND) && compoundTag.contains("ItemSlot2", Constants.NBT.TAG_COMPOUND);
    }

    @Override
    public String getID() {
        return "LongPlate";
    }

    @Override
    public void interact(PlayerEntity player, Hand hand, ItemStack itemStack, int pos, HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel selfPos) {
        if (itemStack.isEmpty()) {
            if (player.isCrouching()) {
                hotpotPlateBlockEntity.tryRemove(this, selfPos);
            } else {
                hotpotPlateBlockEntity.tryTakeOutContentViaHand(pos, selfPos);
            }
        } else {
            SimpleItemSlot preferred = pos == pos1 ? itemSlot1 : itemSlot2;
            SimpleItemSlot fallback = pos == pos1 ? itemSlot2 : itemSlot1;

            if (!preferred.addItem(itemStack)) {
                fallback.addItem(itemStack);
            }
        }
    }

    @Override
    public ItemStack takeOutContent(int pos, HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel selfPos) {
        boolean consume = !hotpotPlateBlockEntity.isInfiniteContent();
        return pos == pos1 ? (itemSlot1.isEmpty() ? itemSlot2.takeItem(consume) : itemSlot1.takeItem(consume)) : itemSlot2.takeItem(consume);
    }

    @Override
    public void onRemove(HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel pos) {
        itemSlot1.dropItem(pos);
        itemSlot2.dropItem(pos);
    }

    @Override
    public void render(TileEntityRendererDispatcher context, HotpotPlaceableBlockEntity hotpotBlockEntity, float partialTick, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int combinedLight, int combinedOverlay) {
        float x1 = IHotpotPlaceable.getSlotX(pos1) + 0.25f;
        float z1 = IHotpotPlaceable.getSlotZ(pos1) + 0.25f;

        float x2 = IHotpotPlaceable.getSlotX(pos2) + 0.25f;
        float z2 = IHotpotPlaceable.getSlotZ(pos2) + 0.25f;

        poseStack.pushPose();
        poseStack.translate((x1 + x2) / 2, 0f, (z1 + z2) / 2);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(direction.toYRot()));
        poseStack.scale(0.8f, 0.8f, 0.8f);

        IBakedModel model = Minecraft.getInstance().getModelManager().getModel(new ResourceLocation(HotpotModEntry.MODID, "block/hotpot_plate_long"));
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.solid()), null, model, 1, 1, 1, combinedLight, combinedOverlay, EmptyModelData.INSTANCE);

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

    public void renderLargePlateItem(TileEntityRendererDispatcher context, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int combinedLight, int combinedOverlay, SimpleItemSlot slot, float x, float z, int index) {
        poseStack.pushPose();
        poseStack.translate(x, 0.12f, z);
        poseStack.mulPose(Vector3f.YN.rotationDegrees(direction.toYRot()));
        poseStack.translate(0f, 0f, -0.14f + index * 0.09f);
        poseStack.mulPose(Vector3f.XN.rotationDegrees(75));
        poseStack.scale(0.35f, 0.35f, 0.35f);

        slot.renderSlot(context, poseStack, bufferSource, combinedLight, combinedOverlay);

        poseStack.popPose();
    }

    @Override
    public boolean tryPlace(int pos, Direction direction) {

        int pos2 = pos + HotpotPlaceables.DIRECTION_TO_POS.get(direction);
        if (isValidPos(pos, pos2)) {
            this.pos1 = pos;
            this.pos2 = pos2;
            this.direction = direction;

            return true;
        }

        return false;
    }

    public boolean isValidPos(int pos1, int pos2) {
        return 0 <= pos1 && pos1 <= 3 && 0 <= pos2 && pos2 <= 3 && pos1 + pos2 != 3;
    }

    @Override
    public List<Integer> getPos() {
        return ImmutableList.of(pos1, pos2);
    }

    @Override
    public int getAnchorPos() {
        return pos1;
    }

    @Override
    public boolean isConflict(int pos) {
        return pos1 == pos || pos2 == pos;
    }
}
