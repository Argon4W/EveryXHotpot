package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import com.github.argon4w.hotpot.soups.recipes.HotpotCookingRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.Optional;

public abstract class AbstractHotpotRecipeContent extends AbstractHotpotItemStackContent {
    public static final RecipeManager.CachedCheck<SingleRecipeInput, HotpotCookingRecipe> HOTPOT_COOKING_RECIPE_QUICK_CHECK = RecipeManager.createCheck(HotpotModEntry.HOTPOT_COOKING_RECIPE.get());

    public AbstractHotpotRecipeContent(ItemStack itemStack, int cookingTime, int cookingProgress, float experience) {
        super(itemStack, cookingTime, cookingProgress, experience);
    }

    public AbstractHotpotRecipeContent(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity) {
        super(itemStack, hotpotBlockEntity);
    }

    public abstract Optional<AbstractCookingRecipe> getRecipe(ItemStack itemStack, LevelBlockPos pos);

    @Override
    public Optional<Integer> remapCookingTime(IHotpotSoupType soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return getAllCookingRecipe(this, soupType, itemStack, pos).map(AbstractCookingRecipe::getCookingTime);
    }

    @Override
    public Optional<Float> remapExperience(IHotpotSoupType soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return getAllCookingRecipe(this, soupType, itemStack, pos).map(AbstractCookingRecipe::getExperience);
    }

    @Override
    public Optional<ItemStack> remapResult(IHotpotSoupType soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return getAllCookingRecipe(this, soupType, itemStack, pos).map(recipe -> recipe.assemble(new SingleRecipeInput(itemStack), pos.registryAccess()));
    }

    public static Optional<AbstractCookingRecipe> getHotpotCookingRecipe(ItemStack itemStack, LevelBlockPos pos, IHotpotSoupType soupType) {
        return AbstractHotpotRecipeContent.HOTPOT_COOKING_RECIPE_QUICK_CHECK.getRecipeFor(new SingleRecipeInput(itemStack), pos.level()).filter(holder -> holder.value().matchesTargetSoup(soupType)).map(RecipeHolder::value);
    }

    public static Optional<AbstractCookingRecipe> getAllCookingRecipe(AbstractHotpotRecipeContent content, IHotpotSoupType soupType, ItemStack itemStack, LevelBlockPos pos) {
        return content.getRecipe(itemStack, pos).or(() -> getHotpotCookingRecipe(itemStack, pos, soupType));
    }
}
