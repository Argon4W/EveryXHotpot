package com.github.argon4w.hotpot.soups.recipes.ingredients;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;

public interface IHotpotSoupIngredientAction {
    void action(int pos, HotpotBlockEntity hotpotBlockEntity, IHotpotContent content, HotpotComponentSoup sourceSoup, HotpotComponentSoup resultSoup, LevelBlockPos selfPos);
    IHotpotSoupIngredientActionSerializer<?> getSerializer();
}
