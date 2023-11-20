package com.github.argon4w.hotpot.placeables;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotPlaceableBlockEntity;
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

import java.util.ArrayList;
import java.util.List;

public class HotpotSmallPlate implements IHotpotPlaceable {
    private int pos;
    private int directionSlot;
    private Direction direction;
    private final SimpleItemSlot itemSlot = new SimpleItemSlot();

    @Override
    public IHotpotPlaceable load(CompoundNBT compoundTag) {
        pos = compoundTag.getByte("Pos");
        directionSlot = compoundTag.getByte("DirectionPos");
        direction = HotpotPlaceables.POS_TO_DIRECTION.get(directionSlot - pos);

        itemSlot.load(compoundTag.getCompound("ItemSlot"));

        return this;
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundTag) {
        compoundTag.putByte("Pos", (byte) pos);
        compoundTag.putByte("DirectionPos", (byte) directionSlot);

        compoundTag.put("ItemSlot", itemSlot.save(new CompoundNBT()));

        return compoundTag;
    }

    @Override
    public boolean isValid(CompoundNBT compoundTag) {
        return compoundTag.contains("Pos", Constants.NBT.TAG_BYTE) && compoundTag.contains("DirectionPos", Constants.NBT.TAG_BYTE) && compoundTag.contains("ItemSlot", Constants.NBT.TAG_COMPOUND);
    }

    @Override
    public String getID() {
        return "SmallPlate";
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
            itemSlot.addItem(itemStack);
        }
    }

    @Override
    public ItemStack takeOutContent(int pos, HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel selfPos) {
        return itemSlot.takeItem(!hotpotPlateBlockEntity.isInfiniteContent());
    }

    @Override
    public void onRemove(HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel pos) {
        itemSlot.dropItem(pos);
    }

    @Override
    public void render(TileEntityRendererDispatcher context, HotpotPlaceableBlockEntity hotpotBlockEntity, float partialTick, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int combinedLight, int combinedOverlay) {
        float x = IHotpotPlaceable.getSlotX(pos) + 0.25f;
        float z = IHotpotPlaceable.getSlotZ(pos) + 0.25f;

        poseStack.pushPose();
        poseStack.translate(x, 0f, z);
        poseStack.scale(0.8f, 0.8f, 0.8f);

        IBakedModel model = Minecraft.getInstance().getModelManager().getModel(new ResourceLocation(HotpotModEntry.MODID, "block/hotpot_plate_small"));
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.translucent()), null, model, 1, 1, 1, combinedLight, combinedOverlay, EmptyModelData.INSTANCE);

        poseStack.popPose();

        for (int i = 0; i < itemSlot.getRenderCount(); i ++) {
            poseStack.pushPose();

            poseStack.translate(x, 0.065f + 0.02 * i, z);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(direction.toYRot() + (i % 2) * 20));
            poseStack.mulPose(Vector3f.XP.rotationDegrees(90f));
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
    public boolean tryPlace(int pos, Direction direction) {
        this.pos = pos;
        this.directionSlot = pos + HotpotPlaceables.DIRECTION_TO_POS.get(direction);
        this.direction = direction;

        return true;
    }

    @Override
    public List<Integer> getPos() {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(pos);
        return list;
    }

    @Override
    public int getAnchorPos() {
        return pos;
    }

    @Override
    public boolean isConflict(int pos) {
        return this.pos == pos;
    }
}
