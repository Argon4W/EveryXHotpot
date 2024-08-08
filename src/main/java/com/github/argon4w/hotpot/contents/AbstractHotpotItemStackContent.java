package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.items.IHotpotSpecialContentItem;
import com.github.argon4w.hotpot.soups.IHotpotSoup;
import com.github.argon4w.hotpot.soups.types.IHotpotSoupWithActiveness;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.joml.Math;

import java.util.Optional;

public abstract class AbstractHotpotItemStackContent implements IHotpotContent {
    private ItemStack itemStack;
    private int cookingTime;
    private float cookingProgress;
    private double experience;

    public AbstractHotpotItemStackContent(ItemStack itemStack, int cookingTime, float cookingProgress, double experience) {
        this.itemStack = itemStack;
        this.cookingTime = cookingTime;
        this.cookingProgress = cookingProgress;
        this.experience = experience;
    }

    public AbstractHotpotItemStackContent(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        this.itemStack = itemStack.split(1);
        this.cookingTime = remapCookingTime(hotpotBlockEntity.getSoup(), this.itemStack, pos, hotpotBlockEntity).orElse(-1);
        this.cookingProgress = 0;
        this.experience = 0;
    }

    public abstract Optional<Integer> remapCookingTime(IHotpotSoup soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity);
    public abstract Optional<ItemStack> remapResult(IHotpotSoup soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity);
    public abstract Optional<Double> remapExperience(IHotpotSoup soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity);

    @Override
    public ItemStack takeOut(Player player, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        if (!(pos.level() instanceof ServerLevel serverLevel)) {
            return itemStack;
        }

        if (hotpotBlockEntity.getSoup() instanceof IHotpotSoupWithActiveness withActiveness) {
            experience *= (1f + withActiveness.getActiveness());
        }

        ExperienceOrb.award(serverLevel, pos.toVec3(), Math.roundHalfUp(experience * 1.5f));

        return itemStack;
    }

    @Override
    public void onOtherContentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        if (!(itemStack.getItem() instanceof IHotpotSpecialContentItem iHotpotSpecialContentItem)) {
            return;
        }

        if (!(content instanceof AbstractHotpotItemStackContent itemStackContent)) {
            return;
        }

        itemStackContent.itemStack = iHotpotSpecialContentItem.onOtherContentUpdate(itemStack, itemStackContent.itemStack, content, hotpotBlockEntity, pos);
        itemStack = iHotpotSpecialContentItem.updateSelf(itemStack, this, hotpotBlockEntity, pos);
    }

    @Override
    public boolean tick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, float ticks) {
        if (cookingTime < 0) {
            return false;
        }

        if (cookingProgress < cookingTime) {
            cookingProgress = Math.max(0.0f, cookingProgress + ticks);
            return false;
        }

        Optional<ItemStack> resultOptional = remapResult(hotpotBlockEntity.getSoup(), itemStack, pos, hotpotBlockEntity);
        cookingTime = -1;

        if (resultOptional.isEmpty()) {
            return false;
        }

        experience = remapExperience(hotpotBlockEntity.getSoup(), itemStack, pos, hotpotBlockEntity).orElse(0d);
        itemStack = resultOptional.get();

        return true;
    }

    @Override
    public boolean shouldRemove(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return false;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public float getCookingProgress() {
        return cookingProgress;
    }

    public double getExperience() {
        return experience;
    }

    public abstract static class Serializer<T extends AbstractHotpotItemStackContent> implements IHotpotContentSerializer<T> {
        public abstract T buildFromData(ItemStack itemStack, int cookingTime, float cookingProgress, double experience);

        @Override
        public MapCodec<T> getCodec() {
            return RecordCodecBuilder.mapCodec(content -> content.group(
                    ItemStack.CODEC.fieldOf("item_stack").forGetter(AbstractHotpotItemStackContent::getItemStack),
                    Codec.INT.fieldOf("cooking_time").forGetter(AbstractHotpotItemStackContent::getCookingTime),
                    Codec.FLOAT.fieldOf("cooking_progress").forGetter(AbstractHotpotItemStackContent::getCookingProgress),
                    Codec.DOUBLE.fieldOf("experience").forGetter(AbstractHotpotItemStackContent::getExperience)
            ).apply(content, this::buildFromData));
        }
    }
}
