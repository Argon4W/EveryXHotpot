package com.github.argon4w.hotpot.soups.recipes.input;

import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record HotpotIngredientRecipeInput(HotpotBlockEntity hotpotBlockEntity) implements RecipeInput {
    @Override
    public ItemStack getItem(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int size() {
        return 1;
    }
}
