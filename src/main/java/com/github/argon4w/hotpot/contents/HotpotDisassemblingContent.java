package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.common.crafting.DifferenceIngredient;
import net.minecraftforge.common.crafting.PartialNBTIngredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class HotpotDisassemblingContent extends AbstractHotpotItemStackContent {
    public static final RandomSource RANDOM_SOURCE = RandomSource.createNewThreadLocalInstance();

    public HotpotDisassemblingContent(ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public boolean shouldRemove(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return getCookingTime() < 0;
    }

    @Override
    public Optional<Integer> remapCookingTime(IHotpotSoupType soupType, ItemStack itemStack, LevelBlockPos pos) {
        return Optional.of(200);
    }

    @Override
    public Optional<Float> remapExperience(IHotpotSoupType soupType, ItemStack itemStack, LevelBlockPos pos) {
        return Optional.of(0.0f);
    }

    @Override
    public Optional<ItemStack> remapResult(IHotpotSoupType soupType, ItemStack itemStack, LevelBlockPos pos) {
        return Optional.of(itemStack);
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(HotpotModEntry.MODID, "disassembling_recipe_content");
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
        List<CraftingRecipe> recipes = pos.level().getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING);

        for (CraftingRecipe recipe : recipes) {
            ItemStack result = recipe.getResultItem(pos.level().registryAccess());

            if (result.isEmpty()) {
                continue;
            }

            if (result.getCount() > 1) {
                continue;
            }

            if (!ItemStack.isSameItemSameTags(result, itemStack)) {
                continue;
            }

            List<Ingredient> ingredients = recipe.getIngredients();

            if (!isIngredientsSafe(ingredients)) {
                continue;
            }

            List<ItemStack> ingredientItemStacks = new ArrayList<>();

            for (Ingredient ingredient : ingredients) {
                ItemStack[] itemStacks = ingredient.getItems();

                if (itemStacks.length == 0) {
                    continue;
                }

                ingredientItemStacks.add(itemStacks[RANDOM_SOURCE.nextInt(itemStacks.length)]);
            }

            return Optional.of(ingredientItemStacks);
        }

        return Optional.empty();
    }

    public boolean isIngredientsSafe(List<Ingredient> ingredients) {
        if (ingredients.size() == 0) {
            return false;
        }

        for (Ingredient ingredient : ingredients) {
            ItemStack[] itemStacks = ingredient.getItems();

            if (itemStacks.length == 0) {
                continue;
            }

            if (hasCraftingRemainingItems(itemStacks)) {
                return false;
            }

            if (ingredient instanceof PartialNBTIngredient) {
                return false;
            }

            if (ingredient instanceof DifferenceIngredient) {
                return false;
            }
        }

        return true;
    }

    public boolean hasCraftingRemainingItems(ItemStack[] itemStacks) {
        return Arrays.stream(itemStacks).map(ItemStack::getCraftingRemainingItem).anyMatch(itemStack -> !itemStack.isEmpty());
    }
}
