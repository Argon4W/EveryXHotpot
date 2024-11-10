package com.github.argon4w.hotpot.soups.recipes;

import com.github.argon4w.hotpot.HotpotModEntry;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractHotpotSoupRecipe<T extends RecipeInput> implements Recipe<T> {

    @NotNull @Override
    public ItemStack getResultItem(@NotNull HolderLookup.Provider access) {
        return ItemStack.EMPTY;
    }

    @NotNull @Override
    public ItemStack assemble(@NotNull T container, @NotNull HolderLookup.Provider access) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @NotNull @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(HotpotModEntry.HOTPOT_BLOCK.get());
    }
}
