package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotCookingRecipeContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public interface IHotpotSpecialHotpotCookingRecipeItem {
    int getCookingTime(ItemStack itemStack,IHotpotSoupType soupType, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity);
    float getExperience(ItemStack itemStack, IHotpotSoupType soupType, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity);
    ItemStack getResult(ItemStack itemStack, IHotpotSoupType soupType, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity);
}
