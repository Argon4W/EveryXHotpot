package com.github.argon4w.hotpot.soups.recipes;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.AbstractHotpotRecipeContent;
import com.github.argon4w.hotpot.soups.IHotpotSoup;
import net.minecraft.world.item.ItemStack;

public interface IHotpotCookingRecipeHolder {
    int getCookingTime(IHotpotSoup soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity, AbstractHotpotRecipeContent content);
    double getExperience(IHotpotSoup soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity, AbstractHotpotRecipeContent content);
    ItemStack getResult(IHotpotSoup soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity, AbstractHotpotRecipeContent content);
}
