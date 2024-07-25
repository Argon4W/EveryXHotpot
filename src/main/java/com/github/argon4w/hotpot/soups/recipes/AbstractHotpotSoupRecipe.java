package com.github.argon4w.hotpot.soups.recipes;

import com.github.argon4w.hotpot.HotpotModEntry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

public abstract class AbstractHotpotSoupRecipe implements Recipe<CraftingInput> {
    @Override
    public ItemStack getResultItem(HolderLookup.Provider access) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean matches(CraftingInput container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(CraftingInput container, HolderLookup.Provider access) {
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
