package com.github.argon4w.hotpot;

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
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.joml.Math;

import java.util.UUID;

public class HotpotPlayerContent implements IHotpotContent {
    public static final String[] VALID_PARTS = {"head", "body", "right_arm", "left_arm", "right_leg", "left_leg"};
    public static final RandomSource RANDOM_SOURCE = RandomSource.createNewThreadLocalInstance();
    public static final float ITEM_ROUND_TRIP_TIME = 60f;
    public static final float ITEM_RADIUS = 0.325f;
    public static final float ITEM_START_Y = 0.45f + 0.5f;
    public static final float ITEM_FLOAT_Y = 0.06f;
    public static final float ITEM_ROTATION = 25f;
    public static final float ITEM_SCALE = 0.25f;

    private GameProfile profile;
    private int modelPartIndex;

    private ModelPart modelPart;
    private ResourceLocation modelSkin;
    private boolean slim;

    public HotpotPlayerContent(Player player) {
        this.profile = player.getGameProfile();
        modelPartIndex = RANDOM_SOURCE.nextInt(VALID_PARTS.length);
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
    public void render(BlockEntityRendererProvider.Context context, HotpotBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float offset) {
        if (modelPart == null) {
            reloadModelPartWithSkin(modelPartIndex);
        }

        poseStack.pushPose();

        float f = blockEntity.getTime() / 20f / ITEM_ROUND_TRIP_TIME + offset;

        poseStack.translate(0.5f + Math.sin(f * 2f * Math.PI) * ITEM_RADIUS, ITEM_START_Y + HotpotItemStackContent.getFloatingCurve(f, 0f) * ITEM_FLOAT_Y, 0.5f + Math.cos(f * 2f * Math.PI) * ITEM_RADIUS);
        poseStack.mulPose(Axis.YP.rotationDegrees(f * 360f));
        poseStack.mulPose(Axis.XP.rotationDegrees(-90f + HotpotItemStackContent.getFloatingCurve(f, 1f) * ITEM_ROTATION));
        poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);

        modelPart.render(poseStack, bufferSource.getBuffer(RenderType.entityTranslucent(modelSkin)), combinedLight, combinedOverlay);

        poseStack.popPose();
    }

    @Override
    public void dropContent(Level level, BlockPos pos) {

    }

    @Override
    public boolean tick(HotpotBlockEntity blockEntity, Level level, BlockPos pos) {
        return false;
    }

    @Override
    public void load(CompoundTag tag) {
        UUID uuid = tag.getUUID("UUID");
        String name = tag.getString("Name");

        profile = new GameProfile(uuid, name);
        modelPartIndex = tag.getInt("ModelPartIndex");
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putUUID("UUID", profile.getId());
        tag.putString("Name", profile.getName());
        tag.putInt("ModelPartIndex", modelPartIndex);

        return tag;
    }

    @Override
    public boolean isValid(CompoundTag tag) {
        return tag.contains("UUID", Tag.TAG_INT_ARRAY) && tag.contains("Name", Tag.TAG_STRING) && tag.contains("ModelPartIndex", Tag.TAG_ANY_NUMERIC);
    }

    @Override
    public String getID() {
        return "Player";
    }
}
