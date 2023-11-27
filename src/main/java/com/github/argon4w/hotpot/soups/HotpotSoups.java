package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.recipes.HotpotSoupFactory;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Optional;
import java.util.function.*;

public class HotpotSoups {
    public static final boolean SINO_FEAST_LOADED = FMLLoader.getLoadingModList().getMods().stream().anyMatch(modInfo -> modInfo.getModId().equals("sinofeast"));
    public static final TagKey<Item> SPICY_ITEM_TAG = ItemTags.create(new ResourceLocation("sinofeast", "tastes/primary/spicy"));
    public static final TagKey<Item> ACRID_ITEM_TAG = ItemTags.create(new ResourceLocation("sinofeast", "tastes/primary/acrid"));
    public static final TagKey<Item> MILK_ITEM_TAG = ItemTags.create(new ResourceLocation("forge", "milk/milk"));
    public static final TagKey<Item> MILK_BOTTLE_ITEM_TAG = ItemTags.create(new ResourceLocation("forge", "milk/milk_bottle"));

    public static final ResourceLocation EMPTY_SOUP_LOCATION = new ResourceLocation(HotpotModEntry.MODID, "empty_soup");

    public static final ResourceKey<Registry<HotpotSoupType<?>>> SOUP_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(HotpotModEntry.MODID, "soup"));
    public static final DeferredRegister<HotpotSoupType<?>> SOUPS = DeferredRegister.create(SOUP_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Supplier<IForgeRegistry<HotpotSoupType<?>>> SOUP_REGISTRY = SOUPS.makeRegistry(() -> new RegistryBuilder<HotpotSoupType<?>>().setDefaultKey(EMPTY_SOUP_LOCATION));

    public static final RegistryObject<HotpotSoupType<HotpotClearSoup>> CLEAR_SOUP = SOUPS.register("clear_soup", () -> HotpotClearSoup::new);
    public static final RegistryObject<HotpotSoupType<HotpotSpicySoup>> SPICY_SOUP = SOUPS.register("spicy_soup", () -> HotpotSpicySoup::new);
    public static final RegistryObject<HotpotSoupType<HotpotCheeseSoup>> CHEESE_SOUP = SOUPS.register("cheese_soup", () -> HotpotCheeseSoup::new);
    public static final RegistryObject<HotpotSoupType<HotpotLavaSoup>> LAVA_SOUP = SOUPS.register("lava_soup", () -> HotpotLavaSoup::new);
    public static final RegistryObject<HotpotSoupType<HotpotEmptySoup>> EMPTY_SOUP = SOUPS.register("empty_soup", () -> HotpotEmptySoup::new);

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
                    .match("spicy_soup")
    );

    public static HotpotSoupType<HotpotEmptySoup> getEmptySoup() {
        return EMPTY_SOUP.get();
    }

    public static IForgeRegistry<HotpotSoupType<?>> getSoupRegistry() {
        return SOUP_REGISTRY.get();
    }

    public static void lookupRecipe(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos, Consumer<IHotpotSoup> consumer) {
        for (BiFunction<HotpotBlockEntity, BlockPosWithLevel, Optional<IHotpotSoup>> recipe : HotpotSoups.HOTPOT_SOUP_RECIPES) {
            Optional<IHotpotSoup> optional = recipe.apply(hotpotBlockEntity, pos);

            if (optional.isPresent()) {
                consumer.accept(optional.get());
                return;
            }
        }
    }
}
