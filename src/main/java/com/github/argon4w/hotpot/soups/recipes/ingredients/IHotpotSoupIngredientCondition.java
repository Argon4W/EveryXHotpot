package com.github.argon4w.hotpot.soups.recipes.ingredients;

import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;

public interface IHotpotSoupIngredientCondition {
    boolean matches(IHotpotContent content, HotpotComponentSoup soup);
    IHotpotSoupIngredientConditionSerializer<?> getSerializer();
}
