package com.github.argon4w.hotpot;

import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotEmptyContent;
import com.github.argon4w.hotpot.contents.HotpotItemStackContent;
import com.github.argon4w.hotpot.contents.HotpotPlayerContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.placeables.*;
import com.github.argon4w.hotpot.soups.HotpotClearSoup;
import com.github.argon4w.hotpot.soups.HotpotEmptySoup;
import com.github.argon4w.hotpot.soups.IHotpotSoup;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;

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
    public static final ConcurrentHashMap<String, Supplier<IHotpotPlaceable>> HOTPOT_PLATE_TYPES = new ConcurrentHashMap<>(Map.of(
            "Empty", HotpotEmptyPlaceable::new,
            "LongPlate", HotpotLongPlate::new,
            "SmallPlate", HotpotSmallPlate::new,
            "PlacedChopstick", HotpotPlacedChopstick::new
    ));
    public static final ConcurrentHashMap<BiPredicate<HotpotBlockEntity, BlockPosWithLevel>, BiFunction<HotpotBlockEntity, BlockPosWithLevel, IHotpotSoup>> HOTPOT_SOUP_MATCHES = new ConcurrentHashMap<>(Map.of(

    ));
    public static final ConcurrentHashMap<Predicate<ItemStack>, HotpotFillReturnable> HOTPOT_EMPTY_FILL_TYPES = new ConcurrentHashMap<>(Map.of(
            (itemStack) -> itemStack.is(Items.WATER_BUCKET), new HotpotFillReturnable(HotpotDefinitions.HOTPOT_SOUP_TYPES.get("ClearSoup"), 1f, SoundEvents.BUCKET_EMPTY, () -> new ItemStack(Items.BUCKET)),
            (itemStack) -> itemStack.is(Items.POTION) && PotionUtils.getPotion(itemStack) == Potions.WATER, new HotpotFillReturnable(HotpotDefinitions.HOTPOT_SOUP_TYPES.get("ClearSoup"), 0.333f, SoundEvents.BOTTLE_FILL, () -> new ItemStack(Items.BUCKET))
    ));
    public static final ConcurrentHashMap<Predicate<ItemStack>, HotpotRefillReturnable> HOTPOT_CLEAR_SOUP_REFILL_TYPES = new ConcurrentHashMap<>(Map.of(
            (itemStack) -> itemStack.is(Items.WATER_BUCKET), new HotpotRefillReturnable(1f, SoundEvents.BUCKET_EMPTY, () -> new ItemStack(Items.BUCKET)),
            (itemStack) -> itemStack.is(Items.POTION) && PotionUtils.getPotion(itemStack) == Potions.WATER, new HotpotRefillReturnable(0.333f, SoundEvents.BOTTLE_FILL, () -> new ItemStack(Items.GLASS_BOTTLE))
    ));
    public static final Map<Integer, Direction> POS_TO_DIRECTION = Map.of(
            - 1, Direction.NORTH,
            + 1, Direction.SOUTH,
            + 2, Direction.EAST,
            - 2, Direction.WEST
    );
    public static final Map<Direction, Integer> DIRECTION_TO_POS = Map.of(
            Direction.NORTH, - 1,
            Direction.SOUTH, + 1,
            Direction.EAST, + 2,
            Direction.WEST, - 2
    );

    public static Supplier<IHotpotContent> getEmptyContent() {
        return HotpotDefinitions.HOTPOT_CONTENT_TYPES.get("Empty");
    }

    public static Supplier<IHotpotSoup> getEmptySoup() {
        return HotpotDefinitions.HOTPOT_SOUP_TYPES.get("Empty");
    }

    public static Supplier<IHotpotPlaceable> getEmptyPlaceable() {
        return HotpotDefinitions.HOTPOT_PLATE_TYPES.get("Empty");
    }

    public static Supplier<IHotpotContent> getContentOrElseEmpty(String key) {
        return HotpotDefinitions.HOTPOT_CONTENT_TYPES.getOrDefault(key, getEmptyContent());
    }

    public static Supplier<IHotpotSoup> getSoupOrElseEmpty(String key) {
        return HotpotDefinitions.HOTPOT_SOUP_TYPES.getOrDefault(key, getEmptySoup());
    }

    public static Supplier<IHotpotPlaceable> getPlaceableOrElseEmpty(String key) {
        return HotpotDefinitions.HOTPOT_PLATE_TYPES.getOrDefault(key, getEmptyPlaceable());
    }

    public static void ifMatchSoup(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos, Consumer<BiFunction<HotpotBlockEntity, BlockPosWithLevel, IHotpotSoup>> consumer) {
        HotpotDefinitions.HOTPOT_SOUP_MATCHES.forEach(((predicate, supplier) -> {
            if (predicate.test(hotpotBlockEntity, pos)) {
                consumer.accept(supplier);
            }
        }));
    }

    public static void ifMatchEmptyFill(ItemStack itemStack, Consumer<HotpotFillReturnable> consumer) {
        Optional<Predicate<ItemStack>> key = HotpotDefinitions.HOTPOT_EMPTY_FILL_TYPES.keySet().stream().filter(predicate -> predicate.test(itemStack)).findFirst();
        key.ifPresent(itemStackPredicate -> consumer.accept(HotpotDefinitions.HOTPOT_EMPTY_FILL_TYPES.get(itemStackPredicate)));
    }

    public static boolean ifMathClearSoupRefill(ItemStack itemStack, Consumer<HotpotRefillReturnable> consumer) {
        Optional<Predicate<ItemStack>> key = HotpotDefinitions.HOTPOT_CLEAR_SOUP_REFILL_TYPES.keySet().stream().filter(predicate -> predicate.test(itemStack)).findFirst();
        if (key.isPresent()) {
            consumer.accept(HotpotDefinitions.HOTPOT_CLEAR_SOUP_REFILL_TYPES.get(key.get()));

            return true;
        }

        return false;
    }

    public static record HotpotFillReturnable(Supplier<IHotpotSoup> soup, float waterLevel, SoundEvent soundEvent, Supplier<ItemStack> returned) {}
    public static record HotpotRefillReturnable(float waterLevel, SoundEvent soundEvent, Supplier<ItemStack> returned) {}
}
