package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.IHotpotSoup;
import com.github.argon4w.hotpot.soups.recipes.HotpotCookingRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;

import java.util.Optional;

public abstract class AbstractHotpotRecipeContent extends AbstractHotpotItemStackContent {
    public static final RecipeManager.CachedCheck<SingleRecipeInput, HotpotCookingRecipe> HOTPOT_COOKING_RECIPE_QUICK_CHECK = RecipeManager.createCheck(HotpotModEntry.HOTPOT_COOKING_RECIPE.get());

    public AbstractHotpotRecipeContent(ItemStack itemStack, int cookingTime, float cookingProgress, double experience) {
        super(itemStack, cookingTime, cookingProgress, experience);
    }

    public AbstractHotpotRecipeContent(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        super(itemStack, hotpotBlockEntity, pos);
    }

    public abstract Optional<AbstractCookingRecipe> getRecipe(ItemStack itemStack, LevelBlockPos pos);

    @Override
    public Optional<Integer> remapCookingTime(IHotpotSoup soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return getAllCookingRecipe(soupType, itemStack, pos).map(AbstractCookingRecipe::getCookingTime);
    }

    @Override
    public Optional<Double> remapExperience(IHotpotSoup soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return getAllCookingRecipe(soupType, itemStack, pos).map(AbstractCookingRecipe::getExperience).map(Double::valueOf);
    }

    @Override
    public Optional<ItemStack> remapResult(IHotpotSoup soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return getAllCookingRecipe(soupType, itemStack, pos).map(recipe -> recipe.assemble(new SingleRecipeInput(itemStack), pos.registryAccess()));
    }

    public Optional<AbstractCookingRecipe> getHotpotCookingRecipe(ItemStack itemStack, LevelBlockPos pos, IHotpotSoup soupType) {
        return AbstractHotpotRecipeContent.HOTPOT_COOKING_RECIPE_QUICK_CHECK.getRecipeFor(new SingleRecipeInput(itemStack), pos.level()).filter(holder -> holder.value().matches(soupType)).map(RecipeHolder::value);
    }

    public Optional<AbstractCookingRecipe> getAllCookingRecipe(IHotpotSoup soupType, ItemStack itemStack, LevelBlockPos pos) {
        return getRecipe(itemStack, pos).or(() -> getHotpotCookingRecipe(itemStack, pos, soupType));
    }
}
