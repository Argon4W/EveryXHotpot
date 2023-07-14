package com.github.argon4w.hotpot;

import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotEmptyContent;
import com.github.argon4w.hotpot.contents.HotpotItemStackContent;
import com.github.argon4w.hotpot.contents.HotpotPlayerContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.*;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
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
    public static final ConcurrentHashMap<BiPredicate<HotpotBlockEntity, BlockPosWithLevel>, BiFunction<HotpotBlockEntity, BlockPosWithLevel, IHotpotSoup>> HOTPOT_SOUP_MATCHES = new ConcurrentHashMap<>(Map.of(

    ));
    public static final ConcurrentHashMap<Item, HotpotFillReturnable> HOTPOT_EMPTY_FILL_TYPES = new ConcurrentHashMap<>(Map.of(
            Items.WATER_BUCKET, new HotpotFillReturnable(HotpotClearSoup::new, () -> new ItemStack(Items.BUCKET))
    ));

    public static Supplier<IHotpotContent> getEmptyContent() {
        return HOTPOT_CONTENT_TYPES.get("Empty");
    }

    public static Supplier<IHotpotSoup> getEmptySoup() {
        return HOTPOT_SOUP_TYPES.get("Empty");
    }

    public static Supplier<IHotpotContent> getContentOrElseEmpty(String key) {
        return HOTPOT_CONTENT_TYPES.getOrDefault(key, getEmptyContent());
    }

    public static Supplier<IHotpotSoup> getSoupOrElseEmpty(String key) {
        return HOTPOT_SOUP_TYPES.getOrDefault(key, getEmptySoup());
    }

    public static void ifMatchSoup(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos, Consumer<BiFunction<HotpotBlockEntity, BlockPosWithLevel, IHotpotSoup>> consumer) {
        HotpotDefinitions.HOTPOT_SOUP_MATCHES.forEach(((predicate, supplier) -> {
            if (predicate.test(hotpotBlockEntity, pos)) {
                consumer.accept(supplier);
            }
        }));
    }

    public static void ifMatchEmptyFill(ItemStack itemStack, Consumer<HotpotFillReturnable> consumer) {
        Item item = itemStack.getItem();

        if (HotpotDefinitions.HOTPOT_EMPTY_FILL_TYPES.containsKey(item)) {
            consumer.accept(HotpotDefinitions.HOTPOT_EMPTY_FILL_TYPES.get(item));
        }
    }

    public static record HotpotFillReturnable(Supplier<IHotpotSoup> soup, Supplier<ItemStack> returned) {}
}
