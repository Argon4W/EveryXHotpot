package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotDefinitions;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import org.joml.Math;

public class HotpotItemStackContent implements IHotpotContent {
    public static final float ITEM_ROUND_TRIP_TIME = 60f;
    public static final float ITEM_RADIUS = 0.315f;
    public static final float ITEM_START_Y = 0.53f;
    public static final float ITEM_FLOAT_Y = 0.06f;
    public static final float ITEM_ROTATION = 25f;
    public static final float ITEM_SCALE = 0.25f;

    private ItemStack itemStack;
    private int cookingTime;
    private int cookingProgress;

    public HotpotItemStackContent(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public HotpotItemStackContent() {}

    @Override
    public void placed(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        this.itemStack = this.itemStack.split(1);
        this.cookingTime = HotpotDefinitions.QUICK_CHECK.getRecipeFor(new SimpleContainer(itemStack), pos.level()).map(AbstractCookingRecipe::getCookingTime).orElse(-1);
        this.cookingProgress = 0;
    }

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
        return itemStack;
    }

    public ItemStack getAssembledContent(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        Container container = new SimpleContainer(itemStack);
        return HotpotDefinitions.QUICK_CHECK.getRecipeFor(container, pos.level()).map((recipe) -> recipe.assemble(container, pos.level().registryAccess())).orElse(itemStack);
    }

    public boolean isAssembled() {
        return cookingTime < 0;
    }

    @Override
    public boolean tick(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        if (cookingTime < 0) return false;

        if (cookingProgress >= cookingTime) {
            ItemStack result = getAssembledContent(hotpotBlockEntity, pos);
            if (result.isItemEnabled(pos.level().enabledFeatures())) {
                itemStack = result;
                cookingTime = -1;

                return true;
            }
        } else {
            cookingProgress ++;
        }

        return false;
    }

    @Override
    public void load(CompoundTag compoundTag) {
        itemStack = ItemStack.of(compoundTag);
        cookingTime = compoundTag.getInt("CookingTime");
        cookingProgress = compoundTag.getInt("CookingProgress");
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        itemStack.save(compoundTag);
        compoundTag.putInt("CookingTime", cookingTime);
        compoundTag.putInt("CookingProgress", cookingProgress);

        return compoundTag;
    }

    @Override
    public boolean isValid(CompoundTag tag) {
        return ItemStack.of(tag) != ItemStack.EMPTY && tag.contains("CookingTime", Tag.TAG_ANY_NUMERIC) && tag.contains("CookingProgress", Tag.TAG_ANY_NUMERIC);
    }

    @Override
    public String getID() {
        return "ItemStack";
    }

    public static float getFloatingCurve(float f, float offset) {
        return (float) Math.sin((f + offset) / 0.25f * 2f  * Math.PI);
    }
}
