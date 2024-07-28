package com.github.argon4w.hotpot.soups.recipes.ingredients;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;

public interface IHotpotSoupIngredientAction {
    IHotpotContent action(LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity, IHotpotContent content, IHotpotSoupType sourceSoup, IHotpotSoupType resultSoup);
    IHotpotSoupIngredientActionSerializer<?> getSerializer();
}
