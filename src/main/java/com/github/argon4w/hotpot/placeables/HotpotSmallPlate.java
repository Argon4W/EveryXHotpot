package com.github.argon4w.hotpot.placeables;

import com.github.argon4w.hotpot.BlockPosWithLevel;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.data.ModelData;

import java.util.List;

public class HotpotSmallPlate implements IHotpotPlaceable {
    private int pos;
    private int directionSlot;
    private Direction direction;
    private final SimpleItemSlot itemSlot = new SimpleItemSlot();

    @Override
    public IHotpotPlaceable load(CompoundTag compoundTag) {
        pos = compoundTag.getByte("Pos");
        directionSlot = compoundTag.getByte("DirectionPos");
        direction = HotpotPlaceables.POS_TO_DIRECTION.get(directionSlot - pos);

        itemSlot.load(compoundTag.getCompound("ItemSlot"));

        return this;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.putByte("Pos", (byte) pos);
        compoundTag.putByte("DirectionPos", (byte) directionSlot);

        compoundTag.put("ItemSlot", itemSlot.save(new CompoundTag()));

        return compoundTag;
    }

    @Override
    public boolean isValid(CompoundTag compoundTag) {
        return compoundTag.contains("Pos", Tag.TAG_BYTE) && compoundTag.contains("DirectionPos", Tag.TAG_BYTE) && compoundTag.contains("ItemSlot", Tag.TAG_COMPOUND);
    }

    @Override
    public String getID() {
        return "SmallPlate";
    }

    @Override
    public void interact(Player player, InteractionHand hand, ItemStack itemStack, int pos, HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel selfPos) {
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
        return itemSlot.takeItem();
    }

    @Override
    public void onRemove(HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel pos) {
        itemSlot.dropItem(pos);
    }

    @Override
    public void render(BlockEntityRendererProvider.Context context, HotpotPlaceableBlockEntity hotpotBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        float x = IHotpotPlaceable.getSlotX(pos) + 0.25f;
        float z = IHotpotPlaceable.getSlotZ(pos) + 0.25f;

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
    public boolean tryPlace(int pos, Direction direction) {
        this.pos = pos;
        this.directionSlot = pos + HotpotPlaceables.DIRECTION_TO_POS.get(direction);
        this.direction = direction;

        return true;
    }

    @Override
    public List<Integer> getPos() {
        return List.of(pos);
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
