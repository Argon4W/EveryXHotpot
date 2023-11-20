package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.common.util.Constants;

import java.util.Random;

public class HotpotPlayerContent implements IHotpotContent {
    public static final Random RANDOM_SOURCE = new Random();
    public static final float ITEM_ROUND_TRIP_TIME = 60f;
    public static final float ITEM_RADIUS = 0.325f;
    public static final float ITEM_START_Y = 0.53f;
    public static final float ITEM_FLOAT_Y = 0.06f;
    public static final float ITEM_ROTATION = 25f;
    public static final float ITEM_SCALE = 0.25f;

    private GameProfile profile;
    private int modelPartIndex;

    private ModelRenderer modelPart;
    private ResourceLocation modelSkin;
    private boolean slim;

    public HotpotPlayerContent(PlayerEntity player, boolean head) {
        this.profile = player.getGameProfile();
        this.modelPartIndex = head ? 0 : (1 + RANDOM_SOURCE.nextInt(5));
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

        Minecraft.getInstance().getEntityRenderDispatcher().renderers.get(EntityType.PLAYER);
        PlayerModel<PlayerEntity> model = new PlayerModel<PlayerEntity>(0.0f, slim);
        modelPart = (new ModelRenderer[] {model.head, model.body, model.rightArm, model.leftArm, model.rightLeg, model.leftLeg})[index];
        modelPart.setPos(0, 0, 0);
        modelPart.zRot = 22.5f;
    }

    @Override
    public void placed(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {

    }

    @Override
    public void render(TileEntityRendererDispatcher context, HotpotBlockEntity hotpotBlockEntity, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int combinedLight, int combinedOverlay, float offset, float waterline) {
        if (modelPart == null) {
            reloadModelPartWithSkin(modelPartIndex);
        }

        poseStack.pushPose();

        float f = hotpotBlockEntity.getTime() / 20f / ITEM_ROUND_TRIP_TIME + offset;

        poseStack.translate(0.5f + Math.sin(f * 2f * Math.PI) * ITEM_RADIUS, ITEM_START_Y + HotpotCampfireRecipeContent.getFloatingCurve(f, 0f) * ITEM_FLOAT_Y + 0.42f * waterline, 0.5f + Math.cos(f * 2f * Math.PI) * ITEM_RADIUS);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(f * 360f));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(-90f + HotpotCampfireRecipeContent.getFloatingCurve(f, 1f) * ITEM_ROTATION));
        poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);

        modelPart.render(poseStack, bufferSource.getBuffer(RenderType.entityTranslucent(modelSkin)), combinedLight, combinedOverlay);

        poseStack.popPose();
    }

    @Override
    public ItemStack takeOut(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        if (modelPartIndex == 0) {
            ItemStack itemStack = new ItemStack(Items.PLAYER_HEAD);
            itemStack.addTagElement("SkullOwner", NBTUtil.writeGameProfile(new CompoundNBT(), profile));

            return itemStack;
        } else {
            return new ItemStack(Items.BONE, RANDOM_SOURCE.nextInt(3));
        }
    }

    @Override
    public void onOtherContentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {

    }

    @Override
    public boolean tick(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return false;
    }

    @Override
    public IHotpotContent load(CompoundNBT tag) {
        profile = NBTUtil.readGameProfile(tag.getCompound("Profile"));
        modelPartIndex = tag.getInt("ModelPartIndex");
        modelPartIndex = Math.min(6 - 1, Math.max(0, modelPartIndex));

        return this;
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag.put("Profile", NBTUtil.writeGameProfile(new CompoundNBT(), profile));
        tag.putInt("ModelPartIndex", modelPartIndex);

        return tag;
    }

    @Override
    public boolean isValid(CompoundNBT tag) {
        return tag.contains("Profile", Constants.NBT.TAG_COMPOUND) && NBTUtil.readGameProfile(tag.getCompound("Profile")) != null && tag.contains("ModelPartIndex", Constants.NBT.TAG_ANY_NUMERIC);
    }

    @Override
    public String getID() {
        return "Player";
    }
}
