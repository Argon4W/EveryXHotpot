package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class HotpotDisassemblingContent extends AbstractHotpotItemStackContent {
    public static final RandomSource RANDOM_SOURCE = RandomSource.createNewThreadLocalInstance();

    public HotpotDisassemblingContent(ItemStack itemStack, int cookingTime, int cookingProgress, float experience) {
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
    public Optional<Float> remapExperience(IHotpotSoupType soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return Optional.of(0.0f);
    }

    @Override
    public Optional<ItemStack> remapResult(IHotpotSoupType soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return Optional.of(itemStack);
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "disassembling_recipe_content");
    }

    @Override
    public IHotpotContentFactory<?> getFactory() {
        return HotpotContents.DISASSEMBLING_RECIPE_CONTENT.get();
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
        public HotpotDisassemblingContent buildFromData(ItemStack itemStack, int cookingTime, int cookingProgress, float experience) {
            return new HotpotDisassemblingContent(itemStack, cookingTime, cookingProgress, experience);
        }

        @Override
        public HotpotDisassemblingContent buildFromItem(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity) {
            return new HotpotDisassemblingContent(itemStack, hotpotBlockEntity);
        }
    }
}
