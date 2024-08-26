package com.github.argon4w.hotpot.soups.recipes.input;

import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record HotpotRecipeInput(ItemStack itemStack, HotpotComponentSoup soup) implements RecipeInput {
    @Override
    public ItemStack getItem(int slot) {
        return itemStack;
    }

    @Override
    public int size() {
        return 1;
    }
}
