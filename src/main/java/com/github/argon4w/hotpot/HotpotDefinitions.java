package com.github.argon4w.hotpot;

import com.github.argon4w.hotpot.contents.HotpotEmptyContent;
import com.github.argon4w.hotpot.contents.HotpotItemStackContent;
import com.github.argon4w.hotpot.contents.HotpotPlayerContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.*;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class HotpotDefinitions {
    public static final RecipeManager.CachedCheck<Container, CampfireCookingRecipe> QUICK_CHECK = RecipeManager.createCheck(RecipeType.CAMPFIRE_COOKING);
    public static final ConcurrentHashMap<String, Supplier<IHotpotContent>> HOTPOT_CONTENT_TYPES = new ConcurrentHashMap<>(Map.of(
            "ItemStack", HotpotItemStackContent::new,
            "Player", HotpotPlayerContent::new,
            "Empty", HotpotEmptyContent::new
    ));
    public static final ConcurrentHashMap<String, Supplier<IHotpotSoup>> HOTPOT_SOUP_TYPES = new ConcurrentHashMap<>(Map.of(
            "ClearSoup", HotpotClearSoup::new,
            "Empty", HotpotEmptySoup::new
    ));
    public static final ConcurrentHashMap<IHotpotSoupPredicate, IHotpotSoupSupplier> HOTPOT_SOUP_MATCHES = new ConcurrentHashMap<>(Map.of(

    ));
    public static final ConcurrentHashMap<Item, HotpotFillReturnable> HOTPOT_EMPTY_FILL_TYPES = new ConcurrentHashMap<>(Map.of(
            Items.WATER_BUCKET, new HotpotFillReturnable(HotpotClearSoup::new, () -> new ItemStack(Items.BUCKET))
    ));

    public static record HotpotFillReturnable(Supplier<IHotpotSoup> soup, Supplier<ItemStack> returned) {}
}
