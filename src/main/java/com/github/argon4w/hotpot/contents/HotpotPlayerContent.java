package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class HotpotPlayerContent implements IHotpotContent {
    public static final String[] VALID_PARTS = {"head", "body", "right_arm", "left_arm", "right_leg", "left_leg"};
    public static final RandomSource RANDOM_SOURCE = RandomSource.createNewThreadLocalInstance();
    public static final float ITEM_ROUND_TRIP_TIME = 60f;
    public static final float ITEM_RADIUS = 0.325f;
    public static final float ITEM_START_Y = 0.53f;
    public static final float ITEM_FLOAT_Y = 0.06f;
    public static final float ITEM_ROTATION = 25f;
    public static final float ITEM_SCALE = 0.25f;

    private GameProfile profile;
    private int modelPartIndex;

    private ModelPart modelPart;
    private ResourceLocation modelSkin;
    private boolean slim;

    public HotpotPlayerContent(Player player, boolean head) {
        this.profile = player.getGameProfile();
        this.modelPartIndex = head ? 0 : RANDOM_SOURCE.nextInt(1, VALID_PARTS.length);
    }

    public HotpotPlayerContent() {}

    private void reloadModelPartWithSkin(int index) {
        slim = DefaultPlayerSkin.getSkinModelName(profile.getId()).equals("slim");
        modelSkin = DefaultPlayerSkin.getDefaultSkin(profile.getId());

        Minecraft.getInstance().getSkinManager().registerSkins(profile, (type, location, texture) -> {
            String modelName = texture.getMetadata("model");
            slim = modelName == null ? this.slim : modelName.equals("slim");
            modelSkin = location;

            updatePlayerModel(index);
        }, true);

        updatePlayerModel(index);
    }

    private void updatePlayerModel(int index) {
        ModelPart playerModelPart = Minecraft.getInstance().getEntityModels().bakeLayer(slim ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER);
        modelPart = playerModelPart.getChild(VALID_PARTS[index]);
        modelPart.setPos(0, 0, 0);
        modelPart.zRot = 22.5f;
    }

    @Override
    public void placed(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {

    }

    @Override
    public void render(BlockEntityRendererProvider.Context context, HotpotBlockEntity hotpotBlockEntity, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float offset, float waterline) {
        if (modelPart == null) {
            reloadModelPartWithSkin(modelPartIndex);
        }

        poseStack.pushPose();

        float f = hotpotBlockEntity.getTime() / 20f / ITEM_ROUND_TRIP_TIME + offset;

        poseStack.translate(0.5f + Math.sin(f * 2f * Math.PI) * ITEM_RADIUS, ITEM_START_Y + HotpotItemStackContent.getFloatingCurve(f, 0f) * ITEM_FLOAT_Y + 0.42f * waterline, 0.5f + Math.cos(f * 2f * Math.PI) * ITEM_RADIUS);
        poseStack.mulPose(Axis.YP.rotationDegrees(f * 360f));
        poseStack.mulPose(Axis.XP.rotationDegrees(-90f + HotpotItemStackContent.getFloatingCurve(f, 1f) * ITEM_ROTATION));
        poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);

        modelPart.render(poseStack, bufferSource.getBuffer(RenderType.entityTranslucent(modelSkin)), combinedLight, combinedOverlay);

        poseStack.popPose();
    }

    @Override
    public ItemStack takeOut(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        if (modelPartIndex == 0) {
            ItemStack itemStack = new ItemStack(Items.PLAYER_HEAD);
            itemStack.addTagElement("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), profile));

            return itemStack;
        } else {
            return new ItemStack(Items.BONE, RANDOM_SOURCE.nextInt(0, 2));
        }
    }

    @Override
    public boolean tick(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return false;
    }

    @Override
    public IHotpotContent load(CompoundTag tag) {
        profile = NbtUtils.readGameProfile(tag.getCompound("Profile"));
        modelPartIndex = tag.getInt("ModelPartIndex");
        modelPartIndex = Math.min(VALID_PARTS.length - 1, Math.max(0, modelPartIndex));

        return this;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.put("Profile", NbtUtils.writeGameProfile(new CompoundTag(), profile));
        tag.putInt("ModelPartIndex", modelPartIndex);

        return tag;
    }

    @Override
    public boolean isValid(CompoundTag tag) {
        return tag.contains("Profile", Tag.TAG_COMPOUND) && NbtUtils.readGameProfile(tag.getCompound("Profile")) != null && tag.contains("ModelPartIndex", Tag.TAG_ANY_NUMERIC);
    }

    @Override
    public String getID() {
        return "Player";
    }
}
