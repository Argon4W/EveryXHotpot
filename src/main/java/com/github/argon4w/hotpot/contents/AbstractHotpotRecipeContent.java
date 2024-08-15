package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.IHotpotSoup;
import com.github.argon4w.hotpot.soups.recipes.HotpotCookingRecipe;
import com.github.argon4w.hotpot.soups.recipes.IHotpotCookingRecipeHolder;
import com.github.argon4w.hotpot.soups.recipes.holder.HotpotCookingRecipeHolder;
import com.github.argon4w.hotpot.soups.recipes.input.HotpotRecipeInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;

import java.util.Optional;

public abstract class AbstractHotpotRecipeContent extends AbstractHotpotItemStackContent {
    public static final RecipeManager.CachedCheck<HotpotRecipeInput, HotpotCookingRecipe> HOTPOT_COOKING_RECIPE_QUICK_CHECK = RecipeManager.createCheck(HotpotModEntry.HOTPOT_COOKING_RECIPE.get());

    public AbstractHotpotRecipeContent(ItemStack itemStack, int cookingTime, float cookingProgress, double experience) {
        super(itemStack, cookingTime, cookingProgress, experience);
    }

    public AbstractHotpotRecipeContent(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        super(itemStack, hotpotBlockEntity, pos);
    }

    public abstract Optional<IHotpotCookingRecipeHolder> getRecipe(ItemStack itemStack, LevelBlockPos pos);

    @Override
    public Optional<Integer> getCookingTime(IHotpotSoup soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return getCookingRecipeHolder(soupType, itemStack, pos).map(holder -> holder.getCookingTime(soupType, itemStack, pos, hotpotBlockEntity, this));
    }

    @Override
    public Optional<Double> getExperience(IHotpotSoup soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return getCookingRecipeHolder(soupType, itemStack, pos).map(holder -> holder.getExperience(soupType, itemStack, pos, hotpotBlockEntity, this));
    }

    @Override
    public Optional<ItemStack> getResult(IHotpotSoup soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return getCookingRecipeHolder(soupType, itemStack, pos).map(holder -> holder.getResult(soupType, itemStack, pos, hotpotBlockEntity, this));
    }

    public Optional<IHotpotCookingRecipeHolder> getHotpotCookingRecipe(ItemStack itemStack, LevelBlockPos pos, IHotpotSoup soupType) {
        return AbstractHotpotRecipeContent.HOTPOT_COOKING_RECIPE_QUICK_CHECK.getRecipeFor(new HotpotRecipeInput(itemStack, soupType), pos.level()).map(RecipeHolder::value).map(HotpotCookingRecipeHolder::new);
    }

    public Optional<IHotpotCookingRecipeHolder> getCookingRecipeHolder(IHotpotSoup soupType, ItemStack itemStack, LevelBlockPos pos) {
        return itemStack.getItem() instanceof IHotpotCookingRecipeHolder holder ? Optional.of(holder) :  getRecipe(itemStack, pos).or(() -> getHotpotCookingRecipe(itemStack, pos, soupType));
    }
}
