package com.github.argon4w.hotpot.client.contents.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.ResolvableProfile;

public class HotpotPlayerModelRenderContext {
    public static final String[] VALID_PARTS = {"head", "body", "right_arm", "left_arm", "right_leg", "left_leg"};

    private final ResolvableProfile profile;
    private final int partIndex;

    private ModelPart modelPart;
    private ResourceLocation modelPartTextureResourceLocation;
    private boolean slim;

    public HotpotPlayerModelRenderContext(ResolvableProfile profile, int partIndex) {
        this.profile = profile;
        this.partIndex = partIndex;
    }

    public void updateModelPartWithTexture() {
        slim = DefaultPlayerSkin.get(profile.gameProfile()).model().id().equals("slim");
        modelPartTextureResourceLocation = DefaultPlayerSkin.getDefaultTexture();

        Minecraft.getInstance().getSkinManager().getOrLoad(profile.gameProfile()).thenAcceptAsync(this::updateModelPartTexture);
        updateModelPart();
    }

    private void updateModelPartTexture(PlayerSkin playerSkin) {
        slim = playerSkin.model().id().equals("slim");
        modelPartTextureResourceLocation = playerSkin.texture();
        updateModelPart();
    }

    private void updateModelPart() {
        ModelPart playerModelPart = Minecraft.getInstance().getEntityModels().bakeLayer(slim ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER);
        modelPart = playerModelPart.getChild(VALID_PARTS[partIndex]);
        modelPart.setPos(0, 0, 0);
        modelPart.zRot = 22.5f;
    }

    public boolean isModelPartLoaded() {
        return modelPart != null;
    }

    public ModelPart getModelPart() {
        return modelPart;
    }

    public ResourceLocation getModelPartTextureResourceLocation() {
        return modelPartTextureResourceLocation;
    }
}
