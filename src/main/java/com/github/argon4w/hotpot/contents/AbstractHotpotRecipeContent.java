package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import com.github.argon4w.hotpot.soups.recipes.HotpotCookingRecipe;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Optional;

public abstract class AbstractHotpotRecipeContent extends AbstractHotpotItemStackContent {
    public static final RecipeManager.CachedCheck<Container, HotpotCookingRecipe> HOTPOT_COOKING_RECIPE_QUICK_CHECK = RecipeManager.createCheck(HotpotModEntry.HOTPOT_COOKING_RECIPE.get());

    public AbstractHotpotRecipeContent(ItemStack itemStack) {
        super(itemStack);
    }

    public AbstractHotpotRecipeContent() {
        super();
    }

    public abstract Optional<? extends AbstractCookingRecipe> getRecipe(ItemStack itemStack, LevelBlockPos pos);

    private Optional<? extends AbstractCookingRecipe> getHotpotCookingRecipe(IHotpotSoupType soupType, ItemStack itemStack, LevelBlockPos pos) {
        Optional<HotpotCookingRecipe> hotpotCookingRecipe = HotpotCookingRecipeContent.HOTPOT_COOKING_RECIPE_QUICK_CHECK.getRecipeFor(new SimpleContainer(itemStack), pos.level());
        Optional<? extends AbstractCookingRecipe> abstractCookingRecipe = getRecipe(itemStack, pos);

        if (hotpotCookingRecipe.isEmpty()) {
            return abstractCookingRecipe;
        }

        if (!hotpotCookingRecipe.get().matchesTargetSoup(soupType)) {
            return abstractCookingRecipe;
        }

        return hotpotCookingRecipe;
    }

    @Override
    public int remapCookingTime(IHotpotSoupType soupType, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return getHotpotCookingRecipe(soupType, itemStack, pos).map(AbstractCookingRecipe::getCookingTime).orElse(-1);
    }

    @Override
    public Optional<Float> remapExperience(IHotpotSoupType soupType, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return getHotpotCookingRecipe(soupType, itemStack, pos).map(AbstractCookingRecipe::getExperience);
    }

    @Override
    public Optional<ItemStack> remapResult(IHotpotSoupType soupType, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return getHotpotCookingRecipe(soupType, itemStack, pos).map(recipe -> recipe.assemble(new SimpleContainer(itemStack), pos.level().registryAccess()));
    }
}
