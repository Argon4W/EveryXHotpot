package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.items.IHotpotSpecialContentItem;
import com.github.argon4w.hotpot.soups.IHotpotSoupWithActiveness;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.joml.Math;

import java.util.Optional;

public abstract class HotpotItemContent implements IHotpotContent {
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

    public HotpotItemContent(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public HotpotItemContent() {
    }

    @Override
    public void placed(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        this.itemStack = this.itemStack.split(1);
        this.cookingTime = remapCookingTime(itemStack, hotpotBlockEntity, pos);
        this.cookingProgress = 0;
        this.experience = 0;
    }

    public abstract int remapCookingTime(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    public abstract Optional<Recipe<Container>> remapRecipe(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);

    @Override
    public void render(BlockEntityRendererProvider.Context context, HotpotBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float offset, float waterline) {
        poseStack.pushPose();

        float f = blockEntity.getTime() / 20f / ITEM_ROUND_TRIP_TIME + offset;

        poseStack.translate(0.5f + Math.sin(f * 2f * Math.PI) * ITEM_RADIUS, ITEM_START_Y + getFloatingCurve(f, 0f) * ITEM_FLOAT_Y + 0.42f * waterline, 0.5f + Math.cos(f * 2f * Math.PI) * ITEM_RADIUS);
        poseStack.mulPose(Axis.YP.rotationDegrees(f * 360f));
        poseStack.mulPose(Axis.XP.rotationDegrees(-90f + getFloatingCurve(f, 1f) * ITEM_ROTATION));
        poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);

        context.getItemRenderer().renderStatic(null, itemStack, ItemDisplayContext.FIXED, true, poseStack, bufferSource, blockEntity.getLevel(), combinedLight, combinedOverlay, ItemDisplayContext.FIXED.ordinal());
        poseStack.popPose();
    }

    @Override
    public ItemStack takeOut(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        if (pos.level() instanceof ServerLevel serverLevel) {
            if (hotpotBlockEntity.getSoup() instanceof IHotpotSoupWithActiveness withActiveness) {
                experience *= (1f + withActiveness.getActiveness(hotpotBlockEntity, pos));
            }

            ExperienceOrb.award(serverLevel, pos.toVec3(), Math.round(experience * 1.5f));
        }

        return itemStack;
    }

    @Override
    public void onOtherContentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        if (itemStack.getItem() instanceof IHotpotSpecialContentItem iHotpotSpecialContentItem && content instanceof HotpotItemContent itemStackContent) {
            itemStackContent.itemStack = iHotpotSpecialContentItem.onOtherContentUpdate(itemStack, itemStackContent.itemStack, content, hotpotBlockEntity, pos);
            itemStack = iHotpotSpecialContentItem.getSelfItemStack(itemStack, this, hotpotBlockEntity, pos);
        }
    }

    @Override
    public boolean tick(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        if (cookingTime < 0) return false;

        if (cookingProgress >= cookingTime) {
            Optional<Recipe<Container>> resultOptional = remapRecipe(itemStack, hotpotBlockEntity, pos);

            if (resultOptional.isPresent()) {
                itemStack = resultOptional.get().assemble(new SimpleContainer(itemStack), pos.level().registryAccess());
                cookingTime = -1;
                experience = resultOptional.get().getExperience();

                return true;
            }
        } else {
            cookingProgress ++;
        }

        return false;
    }

    public Optional<FoodProperties> getFoodProperties() {
        return Optional.ofNullable(itemStack.getFoodProperties(null));
    }

    @Override
    public IHotpotContent load(CompoundTag compoundTag) {
        itemStack = ItemStack.of(compoundTag);

        cookingTime = compoundTag.getInt("CookingTime");
        cookingProgress = compoundTag.getInt("CookingProgress");
        experience = compoundTag.getFloat("Experience");

        return this;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        itemStack.save(compoundTag);

        compoundTag.putInt("CookingTime", cookingTime);
        compoundTag.putInt("CookingProgress", cookingProgress);
        compoundTag.putFloat("Experience", experience);

        return compoundTag;
    }

    @Override
    public boolean isValid(CompoundTag tag) {
        return ItemStack.of(tag) != ItemStack.EMPTY && tag.contains("CookingTime", Tag.TAG_ANY_NUMERIC) && tag.contains("CookingProgress", Tag.TAG_ANY_NUMERIC) && tag.contains("Experience", Tag.TAG_FLOAT);
    }

    @Override
    public String getID() {
        return "ItemStack";
    }

    public static float getFloatingCurve(float f, float offset) {
        return (float) Math.sin((f + offset) / 0.25f * 2f * Math.PI);
    }

    @Override
    public String toString() {
        return itemStack.toString();
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
