package com.github.argon4w.hotpot.placeables;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotPlaceableBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
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
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.List;

public class HotpotLongPlate implements IHotpotPlaceable {
    private int pos1, pos2;
    private final SimpleItemSlot itemSlot1 = new SimpleItemSlot(), itemSlot2 = new SimpleItemSlot();
    private Direction direction;

    @Override
    public IHotpotPlaceable load(CompoundTag compoundTag) {
        pos1 = compoundTag.getByte("Pos1");
        pos2 = compoundTag.getByte("Pos2");
        direction = HotpotPlaceables.POS_TO_DIRECTION.get(pos2 - pos1);

        itemSlot1.load(compoundTag.getCompound("ItemSlot1"));
        itemSlot2.load(compoundTag.getCompound("ItemSlot2"));

        return this;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.putByte("Pos1", (byte) pos1);
        compoundTag.putByte("Pos2", (byte) pos2);

        compoundTag.put("ItemSlot1", itemSlot1.save(new CompoundTag()));
        compoundTag.put("ItemSlot2", itemSlot2.save(new CompoundTag()));

        return compoundTag;
    }

    @Override
    public boolean isValid(CompoundTag compoundTag) {
        return compoundTag.contains("Pos1", Tag.TAG_BYTE) && compoundTag.contains("Pos2", Tag.TAG_BYTE) && compoundTag.contains("ItemSlot1", Tag.TAG_COMPOUND) && compoundTag.contains("ItemSlot2", Tag.TAG_COMPOUND);
    }

    @Override
    public String getID() {
        return "LongPlate";
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
    public void render(BlockEntityRendererProvider.Context context, HotpotPlaceableBlockEntity hotpotBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        float x1 = IHotpotPlaceable.getSlotX(pos1) + 0.25f;
        float z1 = IHotpotPlaceable.getSlotZ(pos1) + 0.25f;

        float x2 = IHotpotPlaceable.getSlotX(pos2) + 0.25f;
        float z2 = IHotpotPlaceable.getSlotZ(pos2) + 0.25f;

        poseStack.pushPose();
        poseStack.translate((x1 + x2) / 2, 0f, (z1 + z2) / 2);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(direction.toYRot()));
        poseStack.scale(0.8f, 0.8f, 0.8f);

        BakedModel model = context.getBlockRenderDispatcher().getBlockModelShaper().getModelManager().getModel(new ResourceLocation(HotpotModEntry.MODID, "block/hotpot_plate_long"));
        context.getBlockRenderDispatcher().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.solid()), null, model, 1, 1, 1, combinedLight, combinedOverlay, EmptyModelData.INSTANCE);

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
        return List.of(pos1, pos2);
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
