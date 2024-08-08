package com.github.argon4w.hotpot.soups.recipes.ingredients;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.IHotpotSoup;

public interface IHotpotSoupIngredientAction {
    void action(int pos, HotpotBlockEntity hotpotBlockEntity, IHotpotContent content, IHotpotSoup sourceSoup, IHotpotSoup resultSoup, LevelBlockPos selfPos);
    IHotpotSoupIngredientActionSerializer<?> getSerializer();
}
