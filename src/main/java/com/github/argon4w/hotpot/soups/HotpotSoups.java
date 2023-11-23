package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.recipes.HotpotSoupAssembler;
import com.github.argon4w.hotpot.soups.recipes.HotpotSoupFactory;
import com.github.argon4w.hotpot.soups.recipes.HotpotSoupMatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;

public class HotpotSoups {
    public static final boolean SINO_FEAST_LOADED = FMLLoader.getLoadingModList().getMods().stream().anyMatch(modInfo -> modInfo.getModId().equals("sinofeast"));
    public static final TagKey<Item> SPICY_ITEM_TAG = ItemTags.create(new ResourceLocation("sinofeast", "tastes/primary/spicy"));
    public static final TagKey<Item> ACRID_ITEM_TAG = ItemTags.create(new ResourceLocation("sinofeast", "tastes/primary/acrid"));
    public static final TagKey<Item> MILK_ITEM_TAG = ItemTags.create(new ResourceLocation("forge", "milk/milk"));
    public static final TagKey<Item> MILK_BOTTLE_ITEM_TAG = ItemTags.create(new ResourceLocation("forge", "milk/milk_bottle"));

    public static final ConcurrentHashMap<String, Supplier<IHotpotSoup>> HOTPOT_SOUP_TYPES = new ConcurrentHashMap<>(Map.of(
            "ClearSoup", HotpotClearSoup::new,
            "SpicySoup", HotpotSpicySoup::new,
            "CheeseSoup", HotpotCheeseSoup::new,
            "LavaSoup", HotpotLavaSoup::new,
            "Empty", HotpotEmptySoup::new
    ));
    public static final List<BiFunction<HotpotBlockEntity, BlockPosWithLevel, Optional<IHotpotSoup>>> HOTPOT_SOUP_RECIPES = List.of(
            (hotpotBlockEntity, pos) -> new HotpotSoupFactory(hotpotBlockEntity)
                    .withVariant(() -> SINO_FEAST_LOADED)
                    .withSoup(soup -> soup instanceof HotpotClearSoup)
                    .withItem(itemStack -> itemStack.is(HotpotSoups.SPICY_ITEM_TAG)).require(6).consume()
                    .withItem(itemStack -> itemStack.is(HotpotSoups.ACRID_ITEM_TAG)).require(2).consume()
                    .withVariant(() -> !SINO_FEAST_LOADED)
                    .withSoup(soup -> soup instanceof HotpotClearSoup)
                    .withItem(itemStack -> itemStack.is(Items.REDSTONE)).require(3).consume()
                    .withItem(itemStack -> itemStack.is(Items.BLAZE_POWDER)).require(3).consume()
                    .withItem(itemStack -> itemStack.is(Items.GUNPOWDER)).require(2).consume()
                    .match("SpicySoup")
    );

    public static Supplier<IHotpotSoup> getEmptySoup() {
        return HotpotSoups.HOTPOT_SOUP_TYPES.get("Empty");
    }

    public static Supplier<IHotpotSoup> getSoupOrElseEmpty(String key) {
        return HotpotSoups.HOTPOT_SOUP_TYPES.getOrDefault(key, getEmptySoup());
    }

    public static void ifMatchSoup(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos, Consumer<IHotpotSoup> consumer) {
        for (BiFunction<HotpotBlockEntity, BlockPosWithLevel, Optional<IHotpotSoup>> recipe : HotpotSoups.HOTPOT_SOUP_RECIPES) {
            Optional<IHotpotSoup> optional = recipe.apply(hotpotBlockEntity, pos);

            if (optional.isPresent()) {
                consumer.accept(optional.get());
                return;
            }
        }
    }
}

