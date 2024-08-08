package com.github.argon4w.hotpot.soups.recipes.input;

import com.github.argon4w.hotpot.soups.IHotpotSoup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record HotpotRecipeInput(ItemStack itemStack, IHotpotSoup soup) implements RecipeInput {
    @Override
    public ItemStack getItem(int slot) {
        return itemStack;
    }

    @Override
    public int size() {
        return 1;
    }
}
