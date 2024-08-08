package com.github.argon4w.hotpot.soups.recipes.ingredients;

import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.IHotpotSoup;

public interface IHotpotSoupIngredientCondition {
    boolean matches(IHotpotContent content, IHotpotSoup soup);
    IHotpotSoupIngredientConditionSerializer<?> getSerializer();
}
