package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.items.IHotpotSpecialContentItem;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import com.github.argon4w.hotpot.soups.IHotpotSoupTypeWithActiveness;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Math;

import java.util.Optional;

public abstract class AbstractHotpotItemStackContent implements IHotpotContent {
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
    public void create(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        this.itemStack = this.itemStack.split(1);
        this.cookingTime = remapCookingTime(hotpotBlockEntity.getSoup(), itemStack, hotpotBlockEntity, pos);
        this.cookingProgress = 0;
        this.experience = 0;
    }

    public abstract int remapCookingTime(IHotpotSoupType soupType, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    public abstract Optional<ItemStack> remapResult(IHotpotSoupType soupType, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    public abstract Optional<Float> remapExperience(IHotpotSoupType soupType, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);

    @Override
    public ItemStack takeOut(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        if (pos.level() instanceof ServerLevel serverLevel) {
            if (hotpotBlockEntity.getSoup() instanceof IHotpotSoupTypeWithActiveness withActiveness) {
                experience *= (1f + withActiveness.getActiveness(hotpotBlockEntity, pos));
            }

            ExperienceOrb.award(serverLevel, pos.toVec3(), Math.round(experience * 1.5f));
        }

        return itemStack;
    }

    @Override
    public void onOtherContentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        if (itemStack.getItem() instanceof IHotpotSpecialContentItem iHotpotSpecialContentItem && content instanceof AbstractHotpotItemStackContent itemStackContent) {
            itemStackContent.itemStack = iHotpotSpecialContentItem.onOtherContentUpdate(itemStack, itemStackContent.itemStack, content, hotpotBlockEntity, pos);
            itemStack = iHotpotSpecialContentItem.getSelfItemStack(itemStack, this, hotpotBlockEntity, pos);
        }
    }

    @Override
    public boolean tick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        if (cookingTime < 0) return false;

        if (cookingProgress >= cookingTime) {
            Optional<ItemStack> resultOptional = remapResult(hotpotBlockEntity.getSoup(), itemStack, hotpotBlockEntity, pos);

            if (resultOptional.isPresent()) {
                experience = remapExperience(hotpotBlockEntity.getSoup(), itemStack, hotpotBlockEntity, pos).orElse(0f);
                itemStack = resultOptional.get();
                cookingTime = -1;

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
    public String toString() {
        return itemStack.toString();
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getCookingTime() {
        return cookingTime;
    }
}
