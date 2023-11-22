package com.github.argon4w.hotpot.placeables;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotPlaceableBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
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

public class HotpotPlacedChopstick implements IHotpotPlaceable {
    private int pos1, pos2;
    private ItemStack chopstickItemStack = ItemStack.EMPTY;
    private Direction direction;

    @Override
    public IHotpotPlaceable load(CompoundTag compoundTag) {
        pos1 = compoundTag.getByte("Pos1");
        pos2 = compoundTag.getByte("Pos2");
        direction = HotpotPlaceables.POS_TO_DIRECTION.get(pos2 - pos1);

        chopstickItemStack = ItemStack.of(compoundTag.getCompound("Chopstick"));

        return this;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.putByte("Pos1", (byte) pos1);
        compoundTag.putByte("Pos2", (byte) pos2);

        compoundTag.put("Chopstick", chopstickItemStack.save(new CompoundTag()));

        return compoundTag;
    }

    @Override
    public boolean isValid(CompoundTag compoundTag) {
        return compoundTag.contains("Pos1", Tag.TAG_BYTE) && compoundTag.contains("Pos2", Tag.TAG_BYTE) && compoundTag.contains("Chopstick", Tag.TAG_COMPOUND);
    }

    @Override
    public String getID() {
        return "PlacedChopstick";
    }

    @Override
    public void interact(Player player, InteractionHand hand, ItemStack itemStack, int pos, HotpotPlaceableBlockEntity hotpotPlateBlockEntity, BlockPosWithLevel selfPos) {
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
    public void render(BlockEntityRendererProvider.Context context, HotpotPlaceableBlockEntity hotpotBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        float x1 = IHotpotPlaceable.getSlotX(pos1) + 0.25f;
        float z1 = IHotpotPlaceable.getSlotZ(pos1) + 0.25f;

        float x2 = IHotpotPlaceable.getSlotX(pos2) + 0.25f;
        float z2 = IHotpotPlaceable.getSlotZ(pos2) + 0.25f;

        poseStack.pushPose();
        poseStack.translate((x1 + x2) / 2, 0.07f, (z1 + z2) / 2);
        poseStack.mulPose(Vector3f.YN.rotationDegrees(direction.toYRot()));
        poseStack.mulPose(Vector3f.XN.rotationDegrees(95));
        poseStack.scale(0.5f, 0.5f, 0.5f);

        context.getItemRenderer().renderStatic(null, chopstickItemStack, ItemTransforms.TransformType.NONE, true, poseStack, bufferSource, null, combinedLight, combinedOverlay, ItemTransforms.TransformType.FIXED.ordinal());

        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate((x1 + x2) / 2, 0f, (z1 + z2) / 2);
        poseStack.mulPose(Vector3f.YN.rotationDegrees(direction.toYRot()));
        poseStack.scale(0.5f, 0.5f, 0.5f);

        BakedModel model = context.getBlockRenderDispatcher().getBlockModelShaper().getModelManager().getModel(new ResourceLocation(HotpotModEntry.MODID, "block/hotpot_chopstick_stand"));
        context.getBlockRenderDispatcher().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.solid()), null, model, 1, 1, 1, combinedLight, combinedOverlay, ModelData.EMPTY, RenderType.solid());

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

    public void setChopstickItemStack(ItemStack chopstickItemStack) {
        this.chopstickItemStack = chopstickItemStack;
    }
}
