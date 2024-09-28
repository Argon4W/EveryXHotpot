package com.github.argon4w.hotpot.soups.recipes.holder;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.AbstractHotpotRecipeContent;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.recipes.HotpotSoupCookingRecipe;
import com.github.argon4w.hotpot.api.soups.recipes.IHotpotCookingRecipeHolder;
import com.github.argon4w.hotpot.soups.recipes.input.HotpotRecipeInput;
import net.minecraft.world.item.ItemStack;

public record HotpotCookingRecipeHolder(HotpotSoupCookingRecipe recipe) implements IHotpotCookingRecipeHolder {
    @Override
    public int getCookingTime(HotpotComponentSoup soup, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity, AbstractHotpotRecipeContent content) {
        return recipe.getCookingTime();
    }

    @Override
    public double getExperience(HotpotComponentSoup soup, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity, AbstractHotpotRecipeContent content) {
        return recipe.getExperience();
    }

    @Override
    public ItemStack getResult(HotpotComponentSoup soup, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity, AbstractHotpotRecipeContent content) {
        return recipe.assemble(new HotpotRecipeInput(itemStack, soup), pos.registryAccess());
    }
}
