package com.github.argon4w.hotpot.soups.recipes;

import com.github.argon4w.hotpot.HotpotModEntry;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;

public abstract class AbstractHotpotSoupRecipe<T extends RecipeInput> implements Recipe<T> {
    @Override
    public ItemStack getResultItem(HolderLookup.Provider access) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean matches(T container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(T container, HolderLookup.Provider access) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(HotpotModEntry.HOTPOT_BLOCK.get());
    }
}
