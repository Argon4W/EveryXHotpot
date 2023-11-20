package com.github.argon4w.hotpot.placeables;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotPlaceableBlockEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
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

public class HotpotPlacedChopstick implements IHotpotPlaceable {
    private int pos1, pos2;
    private ItemStack chopstickItemStack = ItemStack.EMPTY;
    private Direction direction;

    @Override
    public IHotpotPlaceable load(CompoundNBT compoundTag) {
        pos1 = compoundTag.getByte("Pos1");
        pos2 = compoundTag.getByte("Pos2");
        direction = HotpotPlaceables.POS_TO_DIRECTION.get(pos2 - pos1);

        chopstickItemStack = ItemStack.of(compoundTag.getCompound("Chopstick"));

        return this;
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundTag) {
        compoundTag.putByte("Pos1", (byte) pos1);
        compoundTag.putByte("Pos2", (byte) pos2);

        compoundTag.put("Chopstick", chopstickItemStack.save(new CompoundNBT()));

        return compoundTag;
    }

    @Override
    public boolean isValid(CompoundNBT compoundTag) {
        return compoundTag.contains("Pos1", Constants.NBT.TAG_BYTE) && compoundTag.contains("Pos2", Constants.NBT.TAG_BYTE) && compoundTag.contains("Chopstick", Constants.NBT.TAG_COMPOUND);
    }

    @Override
    public String getID() {
        return "PlacedChopstick";
    }

    @Override
    public void interact(PlayerEntity player, Hand hand, ItemStack itemStack, int pos, HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel selfPos) {
        hotpotPlateBlockEntity.tryRemove(this, selfPos);
    }

    @Override
    public ItemStack takeOutContent(int pos, HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel selfPos) {
        return ItemStack.EMPTY;
    }

    @Override
    public void onRemove(HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel pos) {
        pos.dropItemStack(chopstickItemStack);
    }

    @Override
    public void render(TileEntityRendererDispatcher context, HotpotPlaceableBlockEntity hotpotBlockEntity, float partialTick, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int combinedLight, int combinedOverlay) {
        float x1 = IHotpotPlaceable.getSlotX(pos1) + 0.25f;
        float z1 = IHotpotPlaceable.getSlotZ(pos1) + 0.25f;

        float x2 = IHotpotPlaceable.getSlotX(pos2) + 0.25f;
        float z2 = IHotpotPlaceable.getSlotZ(pos2) + 0.25f;

        poseStack.pushPose();
        poseStack.translate((x1 + x2) / 2, 0.07f, (z1 + z2) / 2);
        poseStack.mulPose(Vector3f.YN.rotationDegrees(direction.toYRot()));
        poseStack.mulPose(Vector3f.XN.rotationDegrees(95));
        poseStack.scale(0.5f, 0.5f, 0.5f);

        Minecraft.getInstance().getItemRenderer().renderStatic(null, chopstickItemStack, ItemCameraTransforms.TransformType.NONE, true, poseStack, bufferSource, null, combinedLight, combinedOverlay);

        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate((x1 + x2) / 2, 0f, (z1 + z2) / 2);
        poseStack.mulPose(Vector3f.YN.rotationDegrees(direction.toYRot()));
        poseStack.scale(0.5f, 0.5f, 0.5f);

        IBakedModel model = Minecraft.getInstance().getModelManager().getModel(new ResourceLocation(HotpotModEntry.MODID, "block/hotpot_chopstick_stand"));
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.solid()), null, model, 1, 1, 1, combinedLight, combinedOverlay, EmptyModelData.INSTANCE);

        poseStack.popPose();
    }

    @Override
    public ItemStack getCloneItemStack(HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel level) {
        return chopstickItemStack;
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
        List<Integer> integers = new java.util.ArrayList<>();
        integers.add(pos1);
        integers.add(pos2);
        return integers;
    }

    @Override
    public int getAnchorPos() {
        return pos1;
    }

    @Override
    public boolean isConflict(int pos) {
        return pos1 == pos || pos2 == pos;
    }

    public void setChopstickItemStack(ItemStack chopstickItemStack) {
        this.chopstickItemStack = chopstickItemStack;
    }
}
