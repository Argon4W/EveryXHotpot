package com.github.argon4w.hotpot.client.contents.player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;

public class HotpotPlayerModelRenderContext {
    public static final String[] VALID_PARTS = {"head", "body", "right_arm", "left_arm", "right_leg", "left_leg"};

    private final GameProfile profile;
    private final int partIndex;

    private ModelPart modelPart;
    private ResourceLocation modelPartTextureResourceLocation;
    private boolean slim;

    public HotpotPlayerModelRenderContext(GameProfile profile, int partIndex) {
        this.profile = profile;
        this.partIndex = partIndex;
    }

    public void updateModelPartWithTexture() {
        slim = DefaultPlayerSkin.getSkinModelName(getProfile().getId()).equals("slim");
        modelPartTextureResourceLocation = DefaultPlayerSkin.getDefaultSkin(getProfile().getId());

        Minecraft.getInstance().getSkinManager().registerSkins(getProfile(), this::updateModelPartTexture, true);
        updateModelPart();
    }

    private void updateModelPartTexture(MinecraftProfileTexture.Type type, ResourceLocation resourceLocation, MinecraftProfileTexture texture) {
        String modelName = texture.getMetadata("model");
        slim = modelName == null ? this.slim : modelName.equals("slim");
        modelPartTextureResourceLocation = resourceLocation;

        updateModelPart();
    }

    private void updateModelPart() {
        ModelPart playerModelPart = Minecraft.getInstance().getEntityModels().bakeLayer(slim ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER);
        modelPart = playerModelPart.getChild(VALID_PARTS[getPartIndex()]);
        modelPart.setPos(0, 0, 0);
        modelPart.zRot = 22.5f;
    }

    public GameProfile getProfile() {
        return profile;
    }

    public int getPartIndex() {
        return partIndex;
    }

    public boolean isModelPartLoaded() {
        return modelPart != null;
    }

    public void setModelPart(ModelPart modelPart) {
        this.modelPart = modelPart;
    }

    public ModelPart getModelPart() {
        return modelPart;
    }

    public void setModelPartTextureResourceLocation(ResourceLocation modelPartTextureResourceLocation) {
        this.modelPartTextureResourceLocation = modelPartTextureResourceLocation;
    }

    public ResourceLocation getModelPartTextureResourceLocation() {
        return modelPartTextureResourceLocation;
    }

    public void setSlim(boolean slim) {
        this.slim = slim;
    }

    public boolean isSlim() {
        return slim;
    }
}
