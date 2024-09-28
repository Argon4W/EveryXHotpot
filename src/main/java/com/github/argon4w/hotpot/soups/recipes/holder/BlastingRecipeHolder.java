package com.github.argon4w.hotpot.soups.recipes.holder;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.AbstractHotpotRecipeContent;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.api.soups.recipes.IHotpotCookingRecipeHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;

public record BlastingRecipeHolder(BlastingRecipe recipe) implements IHotpotCookingRecipeHolder {
    @Override
    public int getCookingTime(HotpotComponentSoup soup, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity, AbstractHotpotRecipeContent content) {
        return recipe.getCookingTime() * 2;
    }

    @Override
    public double getExperience(HotpotComponentSoup soup, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity, AbstractHotpotRecipeContent content) {
        return recipe.getExperience();
    }

    @Override
    public ItemStack getResult(HotpotComponentSoup soup, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity, AbstractHotpotRecipeContent content) {
        return recipe.assemble(new SingleRecipeInput(itemStack), pos.registryAccess());
    }
}
