package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.items.IHotpotCustomItemStackUpdaterProvider;
import com.github.argon4w.hotpot.items.IHotpotItemStackUpdater;
import com.github.argon4w.hotpot.items.IHotpotUpdateAwareContentItem;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import org.joml.Math;

import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractHotpotItemStackContent implements IHotpotContent {
    private ItemStack itemStack;
    private int cookingTime;
    private double cookingProgress;
    private double experience;

    public AbstractHotpotItemStackContent(ItemStack itemStack, int cookingTime, double cookingProgress, double experience) {
        this.itemStack = itemStack;
        this.cookingTime = cookingTime;
        this.cookingProgress = cookingProgress;
        this.experience = experience;
    }

    public AbstractHotpotItemStackContent(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        this.itemStack = itemStack.split(1);
        this.cookingTime = getCookingTime(hotpotBlockEntity.getSoup(), this.itemStack, pos, hotpotBlockEntity).orElse(-1);
        this.cookingProgress = 0;
        this.experience = 0;
    }

    public abstract Optional<Integer> getCookingTime(HotpotComponentSoup soup, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity);
    public abstract Optional<ItemStack> getResult(HotpotComponentSoup soup, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity);
    public abstract Optional<Double> getExperience(HotpotComponentSoup soup, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity);

    @Override
    public boolean onTick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, double ticks) {
        if (cookingTime < 0) {
            return false;
        }

        if (cookingProgress < cookingTime) {
            cookingProgress = Math.max(0.0f, cookingProgress + ticks);
            return false;
        }

        Optional<ItemStack> resultOptional = getResult(hotpotBlockEntity.getSoup(), itemStack, pos, hotpotBlockEntity);
        cookingTime = -1;

        if (resultOptional.isEmpty()) {
            return false;
        }

        experience = getExperience(hotpotBlockEntity.getSoup(), itemStack, pos, hotpotBlockEntity).orElse(0d);
        itemStack = resultOptional.get();

        return true;
    }

    @Override
    public void onContentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        if (itemStack.isEmpty()) {
            return;
        }

        if (!(itemStack.getItem() instanceof IHotpotUpdateAwareContentItem updateAware)) {
            return;
        }

        itemStack = updateAware.onContentUpdate(itemStack, content, hotpotBlockEntity, pos);
    }

    @Override
    public ItemStack getContentItemStack(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        hotpotBlockEntity.awardExperience(experience, pos);
        return itemStack;
    }

    @Override
    public boolean shouldRemove(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return false;
    }

    public IHotpotItemStackUpdater getItemStackUpdater() {
        return itemStack.getItem() instanceof IHotpotCustomItemStackUpdaterProvider provider ? provider.getItemStackUpdater() : IHotpotItemStackUpdater.pass();
    }

    public void updateItemStack(Consumer<ItemStack> consumer) {
        itemStack = getItemStackUpdater().update(itemStack, consumer);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public double getCookingProgress() {
        return cookingProgress;
    }

    public double getExperience() {
        return experience;
    }

    public abstract static class Serializer<T extends AbstractHotpotItemStackContent> implements IHotpotContentSerializer<T> {
        public abstract T getFromData(ItemStack itemStack, int cookingTime, double cookingProgress, double experience);

        @Override
        public MapCodec<T> getCodec() {
            return RecordCodecBuilder.mapCodec(content -> content.group(
                    ItemStack.CODEC.fieldOf("item_stack").forGetter(AbstractHotpotItemStackContent::getItemStack),
                    Codec.INT.fieldOf("cooking_time").forGetter(AbstractHotpotItemStackContent::getCookingTime),
                    Codec.DOUBLE.fieldOf("cooking_progress").forGetter(AbstractHotpotItemStackContent::getCookingProgress),
                    Codec.DOUBLE.fieldOf("experience").forGetter(AbstractHotpotItemStackContent::getExperience)
            ).apply(content, this::getFromData));
        }
    }
}
