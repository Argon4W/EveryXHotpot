package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.Math;

public class HotpotItemStackContent implements IHotpotContent {
    public static final float ITEM_ROUND_TRIP_TIME = 60f;
    public static final float ITEM_RADIUS = 0.315f;
    public static final float ITEM_START_Y = 0.45f + 0.5f;
    public static final float ITEM_FLOAT_Y = 0.06f;
    public static final float ITEM_ROTATION = 25f;
    public static final float ITEM_SCALE = 0.25f;

    private ItemStack itemStack;
    private int cookingTime;
    private int cookingProgress;

    public HotpotItemStackContent(ItemStack itemStack, int cookingTime, int cookingProgress) {
        this.itemStack = itemStack;
        this.cookingTime = cookingTime;
        this.cookingProgress = cookingProgress;
    }

    public HotpotItemStackContent() {}

    @Override
    public void render(BlockEntityRendererProvider.Context context, HotpotBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float offset) {
        poseStack.pushPose();

        float f = blockEntity.getTime() / 20f / ITEM_ROUND_TRIP_TIME + offset;

        poseStack.translate(0.5f + Math.sin(f * 2f * Math.PI) * ITEM_RADIUS, ITEM_START_Y + getFloatingCurve(f, 0f) * ITEM_FLOAT_Y, 0.5f + Math.cos(f * 2f * Math.PI) * ITEM_RADIUS);
        poseStack.mulPose(Axis.YP.rotationDegrees(f * 360f));
        poseStack.mulPose(Axis.XP.rotationDegrees(-90f + getFloatingCurve(f, 1f) * ITEM_ROTATION));
        poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);

        context.getItemRenderer().renderStatic(null, itemStack, ItemDisplayContext.FIXED, true, poseStack, bufferSource, blockEntity.getLevel(), combinedLight, combinedOverlay, ItemDisplayContext.FIXED.ordinal());

        poseStack.popPose();
    }

    @Override
    public void dropContent(Level level, BlockPos pos) {
        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), itemStack);
    }

    @Override
    public boolean tick(HotpotBlockEntity blockEntity, Level level, BlockPos pos) {
        if (cookingTime < 0) return false;

        if (cookingProgress >= cookingTime) {
            Container container = new SimpleContainer(itemStack);
            ItemStack result = HotpotBlockEntity.quickCheck.getRecipeFor(container, level).map((recipe) -> recipe.assemble(container, level.registryAccess())).orElse(itemStack);
            if (result.isItemEnabled(level.enabledFeatures())) {
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
    public void load(CompoundTag tag) {
        itemStack = ItemStack.of(tag);
        cookingTime = tag.getInt("CookingTime");
        cookingProgress = tag.getInt("CookingProgress");
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        itemStack.save(tag);
        tag.putInt("CookingTime", cookingTime);
        tag.putInt("CookingProgress", cookingProgress);

        return tag;
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
