package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import com.github.argon4w.hotpot.soups.recipes.HotpotCookingRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;

import java.util.Optional;
import java.util.function.BiFunction;

public abstract class AbstractHotpotRecipeContent extends AbstractHotpotItemStackContent {
    public static final RecipeManager.CachedCheck<SingleRecipeInput, HotpotCookingRecipe> HOTPOT_COOKING_RECIPE_QUICK_CHECK = RecipeManager.createCheck(HotpotModEntry.HOTPOT_COOKING_RECIPE.get());

    public AbstractHotpotRecipeContent(ItemStack itemStack, int cookingTime, float cookingProgress, double experience) {
        super(itemStack, cookingTime, cookingProgress, experience);
    }

    public AbstractHotpotRecipeContent(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity) {
        super(itemStack, hotpotBlockEntity);
    }

    public abstract Optional<AbstractCookingRecipe> getRecipe(ItemStack itemStack, LevelBlockPos pos);

    @Override
    public Optional<Integer> remapCookingTime(IHotpotSoupType soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return getAllCookingRecipe(this::getRecipe, soupType, itemStack, pos).map(AbstractCookingRecipe::getCookingTime);
    }

    @Override
    public Optional<Double> remapExperience(IHotpotSoupType soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return getAllCookingRecipe(this::getRecipe, soupType, itemStack, pos).map(AbstractCookingRecipe::getExperience).map(Double::valueOf);
    }

    @Override
    public Optional<ItemStack> remapResult(IHotpotSoupType soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return getAllCookingRecipe(this::getRecipe, soupType, itemStack, pos).map(recipe -> recipe.assemble(new SingleRecipeInput(itemStack), pos.registryAccess()));
    }

    public static Optional<AbstractCookingRecipe> getHotpotCookingRecipe(ItemStack itemStack, LevelBlockPos pos, IHotpotSoupType soupType) {
        return AbstractHotpotRecipeContent.HOTPOT_COOKING_RECIPE_QUICK_CHECK.getRecipeFor(new SingleRecipeInput(itemStack), pos.level()).filter(holder -> holder.value().matches(soupType)).map(RecipeHolder::value);
    }

    public static Optional<AbstractCookingRecipe> getAllCookingRecipe(BiFunction<ItemStack, LevelBlockPos, Optional<AbstractCookingRecipe>> recipeGetter , IHotpotSoupType soupType, ItemStack itemStack, LevelBlockPos pos) {
        return recipeGetter.apply(itemStack, pos).or(() -> getHotpotCookingRecipe(itemStack, pos, soupType));
    }
}
