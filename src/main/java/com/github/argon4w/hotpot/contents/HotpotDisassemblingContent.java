package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class HotpotDisassemblingContent extends AbstractHotpotItemStackContent {
    public static final RandomSource RANDOM_SOURCE = RandomSource.createNewThreadLocalInstance();

    public HotpotDisassemblingContent(ItemStack itemStack, int cookingTime, float cookingProgress, double experience) {
        super(itemStack, cookingTime, cookingProgress, experience);
    }

    public HotpotDisassemblingContent(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity) {
        super(itemStack, hotpotBlockEntity);
    }

    @Override
    public boolean shouldRemove(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return getCookingTime() < 0;
    }

    @Override
    public Optional<Integer> remapCookingTime(IHotpotSoupType soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return Optional.of(200);
    }

    @Override
    public Optional<Double> remapExperience(IHotpotSoupType soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return Optional.of(0d);
    }

    @Override
    public Optional<ItemStack> remapResult(IHotpotSoupType soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return Optional.of(itemStack);
    }

    @Override
    public Holder<IHotpotContentFactory<?>> getContentFactoryHolder() {
        return HotpotContents.DISASSEMBLING_RECIPE_CONTENT;
    }

    @Override
    public ItemStack takeOut(Player player, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        Optional<List<ItemStack>> optional = getHotpotDisassemblingRecipe(getItemStack(), pos);

        if (getCookingTime() > 0) {
            return super.takeOut(player, hotpotBlockEntity, pos);
        }

        if (hotpotBlockEntity.getWaterLevel() < 0.125f) {
            pos.dropFloatingItemStack(getItemStack());
            return ItemStack.EMPTY;
        }

        if (optional.isEmpty()) {
            pos.dropFloatingItemStack(getItemStack());
            return ItemStack.EMPTY;
        }

        List<ItemStack> itemStacks = optional.get();

        for (ItemStack itemStack : itemStacks) {
            pos.dropFloatingItemStack(itemStack.copy());
        }

        hotpotBlockEntity.setWaterLevel(hotpotBlockEntity.getWaterLevel() - 0.125f);
        return ItemStack.EMPTY;
    }

    public Optional<List<ItemStack>> getHotpotDisassemblingRecipe(ItemStack itemStack, LevelBlockPos pos) {
        List<RecipeHolder<CraftingRecipe>> recipes = pos.level().getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING);

        for (RecipeHolder<CraftingRecipe> holder : recipes) {
            CraftingRecipe recipe = holder.value();
            ItemStack result = recipe.getResultItem(pos.level().registryAccess());

            if (result.isEmpty()) {
                continue;
            }

            if (result.getCount() > 1) {
                continue;
            }

            if (!ItemStack.isSameItemSameComponents(result, itemStack)) {
                continue;
            }

            List<Ingredient> ingredients = recipe.getIngredients();

            if (!isIngredientsSafe(ingredients)) {
                continue;
            }

            return Optional.of(ingredients.stream().filter(Predicate.not(Ingredient::hasNoItems)).map(Ingredient::getItems).map(this::randomItemStack).toList());
        }

        return Optional.empty();
    }

    public ItemStack randomItemStack(ItemStack[] itemStacks) {
        return itemStacks[RANDOM_SOURCE.nextInt(itemStacks.length)];
    }

    public boolean isIngredientsSafe(List<Ingredient> ingredients) {
        return ingredients.isEmpty() || ingredients.stream().allMatch(this::isIngredientSafe);
    }

    public boolean isIngredientSafe(Ingredient ingredient) {
        return !hasCraftingRemainingItems(ingredient) && ingredient.isSimple();
    }

    public boolean hasCraftingRemainingItems(Ingredient ingredient) {
        return !ingredient.hasNoItems() && Arrays.stream(ingredient.getItems()).map(ItemStack::getCraftingRemainingItem).anyMatch(itemStack -> !itemStack.isEmpty());
    }

    public static class Factory extends AbstractHotpotItemStackContent.Factory<HotpotDisassemblingContent> {
        @Override
        public HotpotDisassemblingContent buildFromData(ItemStack itemStack, int cookingTime, float cookingProgress, double experience) {
            return new HotpotDisassemblingContent(itemStack, cookingTime, cookingProgress, experience);
        }

        @Override
        public HotpotDisassemblingContent buildFromItem(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity) {
            return new HotpotDisassemblingContent(itemStack, hotpotBlockEntity);
        }
    }
}
