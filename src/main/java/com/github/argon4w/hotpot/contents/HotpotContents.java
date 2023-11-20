package com.github.argon4w.hotpot.contents;

import com.google.common.collect.ImmutableMap;
import net.minecraft.item.crafting.BlastingRecipe;
import net.minecraft.item.crafting.CampfireCookingRecipe;
import net.minecraft.item.crafting.IRecipeType;

import java.util.HashMap;
import java.util.function.Supplier;

public class HotpotContents {
    public static final IRecipeType<CampfireCookingRecipe> CAMPFIRE_COOKING_RECIPE = IRecipeType.CAMPFIRE_COOKING;
    public static final IRecipeType<BlastingRecipe> BlAST_FURNACE_COOKING_RECIPE = IRecipeType.BLASTING;

    public static final HashMap<String, Supplier<IHotpotContent>> HOTPOT_CONTENT_TYPES = new HashMap<>(ImmutableMap.of(
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
