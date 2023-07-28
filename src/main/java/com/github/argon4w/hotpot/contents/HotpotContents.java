package com.github.argon4w.hotpot.contents;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class HotpotContents {
    public static final RecipeManager.CachedCheck<Container, CampfireCookingRecipe> QUICK_CHECK = RecipeManager.createCheck(RecipeType.CAMPFIRE_COOKING);
    public static final ConcurrentHashMap<String, Supplier<IHotpotContent>> HOTPOT_CONTENT_TYPES = new ConcurrentHashMap<>(Map.of(
            "ItemStack", HotpotItemStackContent::new,
            "Player", HotpotPlayerContent::new,
            "Empty", HotpotEmptyContent::new
    ));

    public static Supplier<IHotpotContent> getEmptyContent() {
        return HotpotContents.HOTPOT_CONTENT_TYPES.get("Empty");
    }

    public static Supplier<IHotpotContent> getContentOrElseEmpty(String key) {
        return HotpotContents.HOTPOT_CONTENT_TYPES.getOrDefault(key, getEmptyContent());
    }
}
