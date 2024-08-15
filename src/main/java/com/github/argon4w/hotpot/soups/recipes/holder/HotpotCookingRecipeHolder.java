package com.github.argon4w.hotpot.soups.recipes.holder;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.AbstractHotpotRecipeContent;
import com.github.argon4w.hotpot.contents.HotpotCookingRecipeContent;
import com.github.argon4w.hotpot.soups.IHotpotSoup;
import com.github.argon4w.hotpot.soups.recipes.HotpotCookingRecipe;
import com.github.argon4w.hotpot.soups.recipes.IHotpotCookingRecipeHolder;
import com.github.argon4w.hotpot.soups.recipes.input.HotpotRecipeInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;

public record HotpotCookingRecipeHolder(HotpotCookingRecipe recipe) implements IHotpotCookingRecipeHolder {
    @Override
    public int getCookingTime(IHotpotSoup soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity, AbstractHotpotRecipeContent content) {
        return recipe.getCookingTime();
    }

    @Override
    public double getExperience(IHotpotSoup soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity, AbstractHotpotRecipeContent content) {
        return recipe.getExperience();
    }

    @Override
    public ItemStack getResult(IHotpotSoup soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity, AbstractHotpotRecipeContent content) {
        return recipe.assemble(new HotpotRecipeInput(itemStack, soupType), pos.registryAccess());
    }
}
