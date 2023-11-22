package com.github.argon4w.hotpot.contents;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class HotpotContents {
    public static final RecipeType<CampfireCookingRecipe> CAMPFIRE_COOKING_RECIPE = RecipeType.CAMPFIRE_COOKING;
    public static final RecipeType<BlastingRecipe> BlAST_FURNACE_COOKING_RECIPE = RecipeType.BLASTING;

    public static final ConcurrentHashMap<String, Supplier<IHotpotContent>> HOTPOT_CONTENT_TYPES = new ConcurrentHashMap<>(Map.of(
            "ItemStack", HotpotCampfireRecipeContent::new,
            "BlastingItemStack", HotpotBlastFurnaceRecipeContent::new,
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
