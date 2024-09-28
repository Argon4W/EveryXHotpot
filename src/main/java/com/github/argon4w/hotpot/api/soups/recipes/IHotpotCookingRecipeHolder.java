package com.github.argon4w.hotpot.api.soups.recipes;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.AbstractHotpotRecipeContent;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import net.minecraft.world.item.ItemStack;

public interface IHotpotCookingRecipeHolder {
    int getCookingTime(HotpotComponentSoup soup, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity, AbstractHotpotRecipeContent content);
    double getExperience(HotpotComponentSoup soup, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity, AbstractHotpotRecipeContent content);
    ItemStack getResult(HotpotComponentSoup soup, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity, AbstractHotpotRecipeContent content);
}
