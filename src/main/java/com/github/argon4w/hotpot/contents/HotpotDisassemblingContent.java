package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.IHotpotResult;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class HotpotDisassemblingContent extends AbstractHotpotItemStackContent {
    public static final RandomSource RANDOM_SOURCE = RandomSource.createNewThreadLocalInstance();
    private final Function<LevelBlockPos, IHotpotResult<CraftingRecipe>> cachedDisassembledResult;

    public HotpotDisassemblingContent(ItemStack itemStack, int cookingTime, double cookingProgress, double experience) {
        super(itemStack, cookingTime, cookingProgress, experience);
        this.cachedDisassembledResult = Util.memoize(this::getDisassembledResultRecipe);
    }

    public HotpotDisassemblingContent(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        super(itemStack, hotpotBlockEntity, pos);
        this.cachedDisassembledResult = Util.memoize(this::getDisassembledResultRecipe);
    }

    @Override
    public boolean shouldRemove(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return getCookingTime() < 0;
    }

    @Override
    public Optional<Integer> getCookingTime(HotpotComponentSoup soup, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return Optional.of(200);
    }

    @Override
    public Optional<Double> getExperience(HotpotComponentSoup soup, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> getResult(HotpotComponentSoup soup, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return Optional.of(itemStack);
    }

    @Override
    public Holder<IHotpotContentSerializer<?>> getContentSerializerHolder() {
        return HotpotContentSerializers.DISASSEMBLING_RECIPE_CONTENT_SERIALIZER;
    }

    public List<ItemStack> getDisassembledResultItemStacks(LevelBlockPos pos) {
        return getCookingTime() > 0 ? List.of(getItemStack()) : getOrCacheDisassembledResultRecipe(pos).map(recipe -> recipe.getIngredients().stream().filter(Predicate.not(Ingredient::hasNoItems)).map(Ingredient::getItems).map(this::randomItemStack).map(ItemStack::copy).toList()).orElse(List.of(getItemStack()));
    }

    public boolean hasDisassembledResult(LevelBlockPos pos) {
        return getOrCacheDisassembledResultRecipe(pos).isPresent();
    }

    public IHotpotResult<CraftingRecipe> getOrCacheDisassembledResultRecipe(LevelBlockPos pos) {
        return cachedDisassembledResult.apply(pos);
    }

    public IHotpotResult<CraftingRecipe> getDisassembledResultRecipe(LevelBlockPos pos) {
        List<RecipeHolder<CraftingRecipe>> recipes = pos.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING);
        List<CraftingRecipe> passedRecipes = new ArrayList<>();

        for (RecipeHolder<CraftingRecipe> holder : recipes) {
            CraftingRecipe recipe = holder.value();
            ItemStack result = recipe.getResultItem(pos.registryAccess());

            if (result.isEmpty()) {
                continue;
            }

            if (result.getCount() > 1) {
                continue;
            }

            if (!ItemStack.isSameItemSameComponents(result, getItemStack())) {
                continue;
            }

            if (!isIngredientsSafe(recipe.getIngredients())) {
                continue;
            }

            passedRecipes.add(recipe);
        }

        return passedRecipes.isEmpty() ? IHotpotResult.pass() : IHotpotResult.success(randomRecipe(passedRecipes));
    }

    public CraftingRecipe randomRecipe(List<CraftingRecipe> recipes) {
        return recipes.get(RANDOM_SOURCE.nextInt(recipes.size()));
    }

    public ItemStack randomItemStack(ItemStack[] itemStacks) {
        return itemStacks[RANDOM_SOURCE.nextInt(itemStacks.length)];
    }

    public boolean isIngredientsSafe(List<Ingredient> ingredients) {
        return ingredients.isEmpty() || ingredients.stream().allMatch(this::isIngredientSafe);
    }

    public boolean isIngredientSafe(Ingredient ingredient) {
        return !hasDurability(ingredient) && !hasCraftingRemainingItems(ingredient) && ingredient.isSimple();
    }

    public boolean hasCraftingRemainingItems(Ingredient ingredient) {
        return !ingredient.hasNoItems() && Arrays.stream(ingredient.getItems()).map(ItemStack::getCraftingRemainingItem).anyMatch(itemStack -> !itemStack.isEmpty());
    }

    public boolean hasDurability(Ingredient ingredient) {
        return !ingredient.hasNoItems() && Arrays.stream(ingredient.getItems()).anyMatch(itemStack -> itemStack.has(DataComponents.MAX_DAMAGE));
    }

    public static class Serializer extends AbstractHotpotItemStackContent.Serializer<HotpotDisassemblingContent> {
        @Override
        public HotpotDisassemblingContent getFromData(ItemStack itemStack, int cookingTime, double cookingProgress, double experience) {
            return new HotpotDisassemblingContent(itemStack, cookingTime, cookingProgress, experience);
        }

        @Override
        public HotpotDisassemblingContent get(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
            return new HotpotDisassemblingContent(itemStack, hotpotBlockEntity, pos);
        }
    }
}
