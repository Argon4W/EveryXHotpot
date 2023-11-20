package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.items.IHotpotSpecialContentItem;
import com.github.argon4w.hotpot.soups.IHotpotSoupWithActiveness;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import java.util.Optional;

public abstract class AbstractHotpotItemStackContent implements IHotpotContent {
    public static final float ITEM_ROUND_TRIP_TIME = 60f;
    public static final float ITEM_RADIUS = 0.315f;
    public static final float ITEM_START_Y = 0.53f;
    public static final float ITEM_FLOAT_Y = 0.06f;
    public static final float ITEM_ROTATION = 25f;
    public static final float ITEM_SCALE = 0.25f;

    private ItemStack itemStack;
    private int cookingTime;
    private int cookingProgress;
    private float experience;

    public AbstractHotpotItemStackContent(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public AbstractHotpotItemStackContent() {
    }

    @Override
    public void placed(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        this.itemStack = this.itemStack.split(1);
        this.cookingTime = remapCookingTime(itemStack, hotpotBlockEntity, pos);
        this.cookingProgress = 0;
        this.experience = 0;
    }

    public abstract int remapCookingTime(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    public abstract Optional<ItemStack> remapResult(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    public abstract Optional<Float> remapExperience(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);

    @Override
    public void render(TileEntityRendererDispatcher context, HotpotBlockEntity blockEntity, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int combinedLight, int combinedOverlay, float offset, float waterline) {
        poseStack.pushPose();

        float f = blockEntity.getTime() / 20f / ITEM_ROUND_TRIP_TIME + offset;

        poseStack.translate(0.5f + Math.sin(f * 2f * Math.PI) * ITEM_RADIUS, ITEM_START_Y + getFloatingCurve(f, 0f) * ITEM_FLOAT_Y + 0.42f * waterline, 0.5f + Math.cos(f * 2f * Math.PI) * ITEM_RADIUS);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(f * 360f));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(-90f + getFloatingCurve(f, 1f) * ITEM_ROTATION));
        poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);

        Minecraft.getInstance().getItemRenderer().renderStatic(null, itemStack, ItemCameraTransforms.TransformType.FIXED, true, poseStack, bufferSource, blockEntity.getLevel(), combinedLight, combinedOverlay);
        poseStack.popPose();
    }

    @Override
    public ItemStack takeOut(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        if (pos.level() instanceof ServerWorld) {
            if (hotpotBlockEntity.getSoup() instanceof IHotpotSoupWithActiveness) {
                experience *= (1f + ((IHotpotSoupWithActiveness) hotpotBlockEntity.getSoup()).getActiveness(hotpotBlockEntity, pos));
            }

            int raw = Math.round(experience * 1.5f);

            while (raw > 0) {
                int value = ExperienceOrbEntity.getExperienceValue(raw);
                raw -= value;
                pos.level().addFreshEntity(new ExperienceOrbEntity(pos.level(), pos.toVec3().x, pos.toVec3().y, pos.toVec3().z, value));
            }
        }

        return itemStack;
    }

    @Override
    public void onOtherContentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        if (itemStack.getItem() instanceof IHotpotSpecialContentItem && content instanceof AbstractHotpotItemStackContent) {
            IHotpotSpecialContentItem iHotpotSpecialContentItem = (IHotpotSpecialContentItem) itemStack.getItem();
            AbstractHotpotItemStackContent itemStackContent = (AbstractHotpotItemStackContent) content;

            itemStackContent.itemStack = iHotpotSpecialContentItem.onOtherContentUpdate(itemStack, itemStackContent.itemStack, content, hotpotBlockEntity, pos);
            itemStack = iHotpotSpecialContentItem.getSelfItemStack(itemStack, this, hotpotBlockEntity, pos);
        }
    }

    @Override
    public boolean tick(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        if (cookingTime < 0) return false;

        if (cookingProgress >= cookingTime) {
            Optional<ItemStack> resultOptional = remapResult(itemStack, hotpotBlockEntity, pos);

            if (resultOptional.isPresent()) {
                experience = remapExperience(itemStack, hotpotBlockEntity, pos).orElse(0f);
                itemStack = resultOptional.get();
                cookingTime = -1;

                return true;
            }
        } else {
            cookingProgress ++;
        }

        return false;
    }

    public Optional<Food> getFoodProperties() {
        return Optional.ofNullable(itemStack.getItem().getFoodProperties());
    }

    @Override
    public IHotpotContent load(CompoundNBT compoundTag) {
        itemStack = ItemStack.of(compoundTag);

        cookingTime = compoundTag.getInt("CookingTime");
        cookingProgress = compoundTag.getInt("CookingProgress");
        experience = compoundTag.getFloat("Experience");

        return this;
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundTag) {
        itemStack.save(compoundTag);

        compoundTag.putInt("CookingTime", cookingTime);
        compoundTag.putInt("CookingProgress", cookingProgress);
        compoundTag.putFloat("Experience", experience);

        return compoundTag;
    }

    @Override
    public boolean isValid(CompoundNBT tag) {
        return ItemStack.of(tag) != ItemStack.EMPTY && tag.contains("CookingTime", Constants.NBT.TAG_ANY_NUMERIC) && tag.contains("CookingProgress", Constants.NBT.TAG_ANY_NUMERIC) && tag.contains("Experience", Constants.NBT.TAG_FLOAT);
    }

    @Override
    public String toString() {
        return itemStack.toString();
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public static float getFloatingCurve(float f, float offset) {
        return (float) Math.sin((f + offset) / 0.25f * 2f * Math.PI);
    }
}
