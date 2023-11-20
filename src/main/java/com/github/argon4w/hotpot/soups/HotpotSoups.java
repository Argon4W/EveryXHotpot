package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.recipes.HotpotSoupAssembler;
import com.github.argon4w.hotpot.soups.recipes.HotpotSoupMatcher;
import com.google.common.collect.ImmutableMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class HotpotSoups {

    public static final ResourceLocation SPICY_ITEM_TAG = new ResourceLocation("sinofeast", "tastes/primary/spicy");
    public static final ResourceLocation ACRID_ITEM_TAG = new ResourceLocation("sinofeast", "tastes/primary/acrid");
    public static final ResourceLocation MILK_ITEM_TAG = new ResourceLocation("forge", "milk/milk");
    public static final ResourceLocation MILK_BOTTLE_ITEM_TAG = new ResourceLocation("forge", "milk/milk");

    public static final HashMap<String, Supplier<IHotpotSoup>> HOTPOT_SOUP_TYPES = new HashMap<>(ImmutableMap.of(
            "ClearSoup", HotpotClearSoup::new,
            "SpicySoup", HotpotSpicySoup::new,
            "CheeseSoup", HotpotCheeseSoup::new,
            "LavaSoup", HotpotLavaSoup::new,
            "Empty", HotpotEmptySoup::new
    ));
    public static final HashMap<BiPredicate<HotpotBlockEntity, BlockPosWithLevel>, BiFunction<HotpotBlockEntity, BlockPosWithLevel, IHotpotSoup>> HOTPOT_SOUP_MATCHES = new HashMap<>(ImmutableMap.of(
            tagExists(SPICY_ITEM_TAG) && tagExists(ACRID_ITEM_TAG) ?
                    (hotpotBlockEntity, pos) -> new HotpotSoupMatcher(hotpotBlockEntity)
                    .withSoup(soup -> soup instanceof HotpotClearSoup)
                    .withItem(itemStack -> hasTag(itemStack, SPICY_ITEM_TAG)).require(6)
                    .withItem(itemStack -> hasTag(itemStack, ACRID_ITEM_TAG)).require(2)
                    .match() :
                    (hotpotBlockEntity, pos) -> new HotpotSoupMatcher(hotpotBlockEntity)
                    .withSoup(soup -> soup instanceof HotpotClearSoup)
                    .withItem(itemStack -> itemStack.getItem().equals(Items.REDSTONE)).require(3)
                    .withItem(itemStack -> itemStack.getItem().equals(Items.BLAZE_POWDER)).require(3)
                    .withItem(itemStack -> itemStack.getItem().equals(Items.GUNPOWDER)).require(2)
                    .match(),
            tagExists(SPICY_ITEM_TAG) && tagExists(ACRID_ITEM_TAG) ?
                    (hotpotBlockEntity, pos) -> new HotpotSoupAssembler(hotpotBlockEntity)
                    .withItem(itemStack -> hasTag(itemStack, SPICY_ITEM_TAG)).consume()
                    .withItem(itemStack -> hasTag(itemStack, ACRID_ITEM_TAG)).consume()
                    .assemble("SpicySoup") :
                    (hotpotBlockEntity, pos) -> new HotpotSoupAssembler(hotpotBlockEntity)
                    .withItem(itemStack -> itemStack.getItem().equals(Items.REDSTONE)).consume()
                    .withItem(itemStack -> itemStack.getItem().equals(Items.BLAZE_POWDER)).consume()
                    .withItem(itemStack -> itemStack.getItem().equals(Items.GUNPOWDER)).consume()
                    .assemble("SpicySoup")
    ));

    public static Supplier<IHotpotSoup> getEmptySoup() {
        return HotpotSoups.HOTPOT_SOUP_TYPES.get("Empty");
    }

    public static Supplier<IHotpotSoup> getSoupOrElseEmpty(String key) {
        return HotpotSoups.HOTPOT_SOUP_TYPES.getOrDefault(key, getEmptySoup());
    }

    public static void ifMatchSoup(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos, Consumer<BiFunction<HotpotBlockEntity, BlockPosWithLevel, IHotpotSoup>> consumer) {
        HotpotSoups.HOTPOT_SOUP_MATCHES.forEach(((predicate, supplier) -> {
            if (predicate.test(hotpotBlockEntity, pos)) {
                consumer.accept(supplier);
            }
        }));
    }

    public static boolean hasTag(ItemStack itemStack, ResourceLocation tagLocation) {
        Item item = itemStack.getItem();
        return ItemTags.getAllTags().getTagOrEmpty(tagLocation).contains(item);
    }

    public static boolean tagExists(ResourceLocation tagLocation) {
        return ItemTags.getAllTags().getTag(tagLocation) != null;
    }
}
