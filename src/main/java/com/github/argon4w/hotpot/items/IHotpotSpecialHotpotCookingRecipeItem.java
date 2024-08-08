package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotCookingRecipeContent;
import com.github.argon4w.hotpot.soups.IHotpotSoup;
import net.minecraft.world.item.ItemStack;

public interface IHotpotSpecialHotpotCookingRecipeItem {
    int getCookingTime(IHotpotSoup soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity, HotpotCookingRecipeContent content);
    double getExperience(IHotpotSoup soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity, HotpotCookingRecipeContent content);
    ItemStack getResult(IHotpotSoup soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity, HotpotCookingRecipeContent content);
}
