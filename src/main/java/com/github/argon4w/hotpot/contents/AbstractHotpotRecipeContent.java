package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.recipes.IHotpotCookingRecipeHolder;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public abstract class AbstractHotpotRecipeContent extends AbstractHotpotItemStackContent {


    public AbstractHotpotRecipeContent(ItemStack itemStack, ItemStack originalItemStack, int cookingTime, double cookingProgress, double experience) {
        super(itemStack, originalItemStack, cookingTime, cookingProgress, experience);
    }

    public AbstractHotpotRecipeContent(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        super(itemStack, hotpotBlockEntity, pos);
    }

    public abstract Optional<IHotpotCookingRecipeHolder> getRecipe(HotpotComponentSoup soup, ItemStack itemStack, LevelBlockPos pos);

    @Override
    public Optional<Integer> getCookingTime(HotpotComponentSoup soup, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return getRecipeHolder(soup, itemStack, pos).map(holder -> holder.getCookingTime(soup, itemStack, pos, hotpotBlockEntity, this));
    }

    @Override
    public Optional<Double> getExperience(HotpotComponentSoup soup, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return getRecipeHolder(soup, itemStack, pos).map(holder -> holder.getExperience(soup, itemStack, pos, hotpotBlockEntity, this));
    }

    @Override
    public Optional<ItemStack> getResult(HotpotComponentSoup soup, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return getRecipeHolder(soup, itemStack, pos).map(holder -> holder.getResult(soup, itemStack, pos, hotpotBlockEntity, this));
    }

    public Optional<IHotpotCookingRecipeHolder> getRecipeHolder(HotpotComponentSoup soup, ItemStack itemStack, LevelBlockPos pos) {
        return itemStack.getItem() instanceof IHotpotCookingRecipeHolder holder ? Optional.of(holder) :  getRecipe(soup, itemStack, pos);
    }
}
