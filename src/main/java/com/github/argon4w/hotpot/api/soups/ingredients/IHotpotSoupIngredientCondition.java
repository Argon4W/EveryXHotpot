package com.github.argon4w.hotpot.api.soups.ingredients;

import com.github.argon4w.hotpot.api.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;

public interface IHotpotSoupIngredientCondition {
    boolean matches(IHotpotContent content, HotpotComponentSoup soup);
    IHotpotSoupIngredientConditionSerializer<?> getSerializer();
}
