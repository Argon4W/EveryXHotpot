package com.github.argon4w.hotpot;

import com.github.argon4w.hotpot.blocks.*;
import com.github.argon4w.hotpot.client.contents.HotpotContentRenderers;
import com.github.argon4w.hotpot.client.contents.HotpotItemContentSpecialRenderers;
import com.github.argon4w.hotpot.client.items.HotpotBlockEntityWithoutLevelRenderer;
import com.github.argon4w.hotpot.client.items.HotpotItemSpecialRenderers;
import com.github.argon4w.hotpot.client.items.process.HotpotSpriteProcessors;
import com.github.argon4w.hotpot.client.placements.HotpotPlacementRenderers;
import com.github.argon4w.hotpot.client.soups.HotpotSoupCustomElements;
import com.github.argon4w.hotpot.client.soups.HotpotSoupRendererConfigManager;
import com.github.argon4w.hotpot.contents.HotpotContents;
import com.github.argon4w.hotpot.items.*;
import com.github.argon4w.hotpot.items.components.*;
import com.github.argon4w.hotpot.placements.*;
import com.github.argon4w.hotpot.recipes.HotpotNapkinHolderDyeRecipe;
import com.github.argon4w.hotpot.recipes.HotpotSkewerRecipe;
import com.github.argon4w.hotpot.recipes.HotpotSpicePackRecipe;
import com.github.argon4w.hotpot.soups.HotpotSoupTypeFactoryManager;
import com.github.argon4w.hotpot.soups.recipes.HotpotCookingRecipe;
import com.github.argon4w.hotpot.soups.recipes.HotpotSoupBaseRecipe;
import com.github.argon4w.hotpot.soups.recipes.HotpotSoupIngredientRecipe;
import com.github.argon4w.hotpot.soups.recipes.HotpotSoupRechargeRecipe;
import com.github.argon4w.hotpot.soups.recipes.ingredients.HotpotSoupIngredients;
import com.github.argon4w.hotpot.soups.HotpotSoupTypes;
import com.mojang.datafixers.DSL;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import org.slf4j.Logger;

import java.util.function.Function;

@Mod(HotpotModEntry.MODID)
public class HotpotModEntry {
    public static final String MODID = "everyxhotpot";
    public static final String HOTPOT_NETWORK_PROTOCOL_VERSION = "3";
    public static final int HOTPOT_SPRITE_TINT_INDEX = 6;

    public static final DeferredBlock<HotpotBlock> HOTPOT_BLOCK = HotpotRegistries.BLOCKS.register("hotpot", HotpotBlock::new);
    public static final DeferredBlock<HotpotPlacementBlock> HOTPOT_PLACEMENT = HotpotRegistries.BLOCKS.register("hotpot_placement", HotpotPlacementBlock::new);
    public static final DeferredBlock<Block> HOTPOT_PLACEMENT_RACK = HotpotRegistries.BLOCKS.register("hotpot_placement_rack", HotpotPlacementRackBlock::new);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HotpotBlockEntity>> HOTPOT_BLOCK_ENTITY = HotpotRegistries.BLOCK_ENTITY_TYPES.register("hotpot",
            () -> BlockEntityType.Builder.of(HotpotBlockEntity::new, HOTPOT_BLOCK.get()).build(DSL.remainderType()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HotpotPlacementBlockEntity>> HOTPOT_PLACEMENT_BLOCK_ENTITY = HotpotRegistries.BLOCK_ENTITY_TYPES.register("hotpot_placement",
            () -> BlockEntityType.Builder.of(HotpotPlacementBlockEntity::new, HOTPOT_PLACEMENT.get()).build(DSL.remainderType()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HotpotPlacementRackBlockEntity>> HOTPOT_PLACEMENT_RACK_BLOCK_ENTITY = HotpotRegistries.BLOCK_ENTITY_TYPES.register("hotpot_placement_rack",
            () -> BlockEntityType.Builder.of(HotpotPlacementRackBlockEntity::new, HOTPOT_PLACEMENT_RACK.get()).build(DSL.remainderType()));

    public static final DeferredItem<HotpotPaperBowlItem> HOTPOT_PAPER_BOWL = HotpotRegistries.ITEMS.register("hotpot_paper_bowl", HotpotPaperBowlItem::new);
    public static final DeferredItem<HotpotSkewerItem> HOTPOT_SKEWER = HotpotRegistries.ITEMS.register("hotpot_skewer", HotpotSkewerItem::new);
    public static final DeferredItem<HotpotSpoonItem> HOTPOT_SLOTTED_SPOON = HotpotRegistries.ITEMS.register("hotpot_slotted_spoon", () -> new HotpotSpoonItem(true));
    public static final DeferredItem<HotpotSpoonItem> HOTPOT_SOUP_SPOON = HotpotRegistries.ITEMS.register("hotpot_soup_spoon", () -> new HotpotSpoonItem(false));
    public static final DeferredItem<BlockItem> HOTPOT_BLOCK_ITEM = HotpotRegistries.ITEMS.register("hotpot", () -> new BlockItem(HOTPOT_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> HOTPOT_PLACEMENT_RACK_BLOCK_ITEM = HotpotRegistries.ITEMS.register("hotpot_placement_rack", () -> new BlockItem(HOTPOT_PLACEMENT_RACK.get(), new Item.Properties()));
    public static final DeferredItem<HotpotPlateItem<HotpotSmallPlate>> HOTPOT_SMALL_PLATE = HotpotRegistries.ITEMS.register("hotpot_small_plate", () -> new HotpotPlateItem<>(HotpotPlacements.SMALL_PLATE));
    public static final DeferredItem<HotpotPlateItem<HotpotLongPlate>> HOTPOT_LONG_PLATE = HotpotRegistries.ITEMS.register("hotpot_long_plate", () -> new HotpotPlateItem<>(HotpotPlacements.LONG_PLATE));
    public static final DeferredItem<HotpotPlateItem<HotpotLargeRoundPlate>> HOTPOT_LARGE_ROUND_PLATE = HotpotRegistries.ITEMS.register("hotpot_large_round_plate", () -> new HotpotPlateItem<>(HotpotPlacements.LARGE_ROUND_PLATE));
    public static final DeferredItem<HotpotNapkinHolderItem> HOTPOT_NAPKIN_HOLDER = HotpotRegistries.ITEMS.register("hotpot_napkin_holder", HotpotNapkinHolderItem::new);
    public static final DeferredItem<HotpotChopstickItem> HOTPOT_CHOPSTICK = HotpotRegistries.ITEMS.register("hotpot_chopstick", HotpotChopstickItem::new);
    public static final DeferredItem<HotpotSpicePackItem> HOTPOT_SPICE_PACK = HotpotRegistries.ITEMS.register("hotpot_spice_pack", HotpotSpicePackItem::new);

    public static final DeferredHolder<MobEffect, MobEffect> HOTPOT_WARM = HotpotRegistries.MOB_EFFECTS.register("warm", () -> new HotpotMobEffect(MobEffectCategory.BENEFICIAL, (240 << 16) | (240 << 8) | 240));
    public static final DeferredHolder<MobEffect, MobEffect> HOTPOT_GREASY = HotpotRegistries.MOB_EFFECTS.register("greasy", () -> new HotpotMobEffect(MobEffectCategory.HARMFUL, (235 << 16) | (235 << 8) | 25));
    public static final DeferredHolder<MobEffect, MobEffect> HOTPOT_ACRID = HotpotRegistries.MOB_EFFECTS.register("acrid", () -> new HotpotMobEffect(MobEffectCategory.BENEFICIAL, (240 << 16) | (84 << 8) | 64).addAttributeModifier(Attributes.ATTACK_SPEED, ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "acrid"), 0.5f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<HotpotSpicePackRecipe>> HOTPOT_SPICE_PACK_SPECIAL_RECIPE = HotpotRegistries.RECIPE_SERIALIZERS.register("crafting_special_hotpot_spice_pack", () -> new SimpleCraftingRecipeSerializer<>(HotpotSpicePackRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<HotpotSkewerRecipe>> HOTPOT_SKEWER_SPECIAL_RECIPE = HotpotRegistries.RECIPE_SERIALIZERS.register("crafting_special_hotpot_skewer", () -> new SimpleCraftingRecipeSerializer<>(HotpotSkewerRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<HotpotNapkinHolderDyeRecipe>> HOTPOT_NAPKIN_HOLDER_DYE_SPECIAL_RECIPE = HotpotRegistries.RECIPE_SERIALIZERS.register("crafting_special_hotpot_napkin_holder_dye", () -> new SimpleCraftingRecipeSerializer<>(HotpotNapkinHolderDyeRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, HotpotSoupIngredientRecipe.Serializer> HOTPOT_SOUP_INGREDIENT_RECIPE_SERIALIZER = HotpotRegistries.RECIPE_SERIALIZERS.register("hotpot_soup_ingredient_recipe", HotpotSoupIngredientRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, HotpotSoupBaseRecipe.Serializer> HOTPOT_SOUP_BASE_RECIPE_SERIALIZER = HotpotRegistries.RECIPE_SERIALIZERS.register("hotpot_soup_base_recipe", HotpotSoupBaseRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, HotpotSoupRechargeRecipe.Serializer> HOTPOT_SOUP_RECHARGE_RECIPE_SERIALIZER = HotpotRegistries.RECIPE_SERIALIZERS.register("hotpot_soup_recharge_recipe", HotpotSoupRechargeRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, HotpotCookingRecipe.Serializer> HOTPOT_COOKING_RECIPE_SERIALIZER = HotpotRegistries.RECIPE_SERIALIZERS.register("hotpot_cooking_recipe", HotpotCookingRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<HotpotSoupIngredientRecipe>> HOTPOT_SOUP_INGREDIENT_RECIPE_TYPE = HotpotRegistries.RECIPE_TYPES.register("hotpot_soup_ingredient_recipe_type", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "hotpot_soup_ingredient_recipe_type")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<HotpotSoupBaseRecipe>> HOTPOT_SOUP_BASE_RECIPE_TYPE = HotpotRegistries.RECIPE_TYPES.register("hotpot_soup_base_recipe_type", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "hotpot_soup_base_recipe_type")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<HotpotSoupRechargeRecipe>> HOTPOT_SOUP_RECHARGE_RECIPE_TYPE = HotpotRegistries.RECIPE_TYPES.register("hotpot_soup_recharge_recipe_type", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "hotpot_soup_recharge_recipe_type")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<HotpotCookingRecipe>> HOTPOT_COOKING_RECIPE = HotpotRegistries.RECIPE_TYPES.register("hotpot_cooking_recipe_type", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "hotpot_cooking_recipe_type")));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<HotpotChopstickDataComponent>> HOTPOT_CHOPSTICK_DATA_COMPONENT = HotpotRegistries.DATA_COMPONENT_TYPES.registerComponentType("chopstick_data_component", builder -> builder.persistent(HotpotChopstickDataComponent.CODEC).networkSynchronized(HotpotChopstickDataComponent.STREAM_CODEC).cacheEncoding());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<HotpotSkewerDataComponent>> HOTPOT_SKEWER_DATA_COMPONENT = HotpotRegistries.DATA_COMPONENT_TYPES.registerComponentType("skewer_data_component", builder -> builder.persistent(HotpotSkewerDataComponent.CODEC).networkSynchronized(HotpotSkewerDataComponent.STREAM_CODEC).cacheEncoding());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<HotpotPaperBowlDataComponent>> HOTPOT_PAPER_BOWL_DATA_COMPONENT = HotpotRegistries.DATA_COMPONENT_TYPES.registerComponentType("paper_bowl_data_component", builder -> builder.persistent(HotpotPaperBowlDataComponent.CODEC).networkSynchronized(HotpotPaperBowlDataComponent.STREAM_CODEC).cacheEncoding());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<HotpotFoodEffectsDataComponent>> HOTPOT_FOOD_EFFECTS_DATA_COMPONENT = HotpotRegistries.DATA_COMPONENT_TYPES.registerComponentType("food_effects_data_component", builder -> builder.persistent(HotpotFoodEffectsDataComponent.CODEC).networkSynchronized(HotpotFoodEffectsDataComponent.STREAM_CODEC).cacheEncoding());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<HotpotSpicePackDataComponent>> HOTPOT_SPICE_PACK_DATA_COMPONENT = HotpotRegistries.DATA_COMPONENT_TYPES.registerComponentType("spice_pack_data_component", builder -> builder.persistent(HotpotSpicePackDataComponent.CODEC).networkSynchronized(HotpotSpicePackDataComponent.STREAM_CODEC).cacheEncoding());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<HotpotSoupDataComponent>> HOTPOT_SOUP_DATA_COMPONENT = HotpotRegistries.DATA_COMPONENT_TYPES.registerComponentType("soup_data_component", builder -> builder.persistent(HotpotSoupDataComponent.CODEC).networkSynchronized(HotpotSoupDataComponent.STREAM_CODEC).cacheEncoding());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<HotpotSpriteProcessorDataComponent>> HOTPOT_SPRITE_PROCESSOR_DATA_COMPONENT = HotpotRegistries.DATA_COMPONENT_TYPES.registerComponentType("sprite_processor_data_component", builder -> builder.persistent(HotpotSpriteProcessorDataComponent.CODEC).networkSynchronized(HotpotSpriteProcessorDataComponent.STREAM_CODEC).cacheEncoding());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<HotpotNapkinHolderDataComponent>> HOTPOT_NAPKIN_HOLDER_DATA_COMPONENT = HotpotRegistries.DATA_COMPONENT_TYPES.registerComponentType("napkin_holder_data_component", builder -> builder.persistent(HotpotNapkinHolderDataComponent.CODEC).networkSynchronized(HotpotNapkinHolderDataComponent.STREAM_CODEC).cacheEncoding());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> HOTPOT_TAB = HotpotRegistries.CREATIVE_MODE_TABS.register("every_x_hotpot_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(() -> HOTPOT_BLOCK_ITEM.get().getDefaultInstance())
            .title(Component.translatable("itemGroup.EveryXHotpot"))
            .displayItems((parameters, output) -> {
                output.accept(HOTPOT_BLOCK_ITEM.get());
                output.accept(HOTPOT_PLACEMENT_RACK_BLOCK_ITEM.get());
                output.accept(HOTPOT_CHOPSTICK.get());
                output.accept(HOTPOT_SMALL_PLATE.get());
                output.accept(HOTPOT_LONG_PLATE.get());
                output.accept(HOTPOT_LARGE_ROUND_PLATE.get());
                output.accept(HOTPOT_NAPKIN_HOLDER.get());
                output.accept(HOTPOT_SPICE_PACK.get());
                output.accept(HOTPOT_SLOTTED_SPOON.get());
                output.accept(HOTPOT_SOUP_SPOON.get());
                output.accept(HOTPOT_PAPER_BOWL.get());
                output.accept(HOTPOT_SKEWER.get());
            }).build());

    public static final ResourceKey<DamageType> IN_HOTPOT_DAMAGE_KEY = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(MODID, "in_hotpot"));
    public static final Function<Level, Holder<DamageType>> IN_HOTPOT_DAMAGE_TYPE = level -> level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(IN_HOTPOT_DAMAGE_KEY);

    public static final HotpotSoupTypeFactoryManager HOTPOT_SOUP_FACTORY_MANAGER = new HotpotSoupTypeFactoryManager();

    public static HotpotBlockEntityWithoutLevelRenderer HOTPOT_SPECIAL_ITEM_RENDERER;
    public static HotpotSoupRendererConfigManager HOTPOT_SOUP_RENDERER_CONFIG_MANAGER;

    public HotpotModEntry(IEventBus modEventBus, ModContainer modContainer) {
        HotpotRegistries.BLOCKS.register(modEventBus);
        HotpotRegistries.ITEMS.register(modEventBus);
        HotpotRegistries.CREATIVE_MODE_TABS.register(modEventBus);
        HotpotRegistries.BLOCK_ENTITY_TYPES.register(modEventBus);
        HotpotRegistries.RECIPE_SERIALIZERS.register(modEventBus);
        HotpotRegistries.RECIPE_TYPES.register(modEventBus);
        HotpotRegistries.MOB_EFFECTS.register(modEventBus);
        HotpotRegistries.DATA_COMPONENT_TYPES.register(modEventBus);

        HotpotSoupTypes.SOUPS.register(modEventBus);
        HotpotSoupCustomElements.CUSTOM_ELEMENTS.register(modEventBus);
        HotpotContents.CONTENTS.register(modEventBus);
        HotpotContentRenderers.CONTENT_RENDERERS.register(modEventBus);
        HotpotItemContentSpecialRenderers.ITEM_CONTENT_SPECIAL_RENDERERS.register(modEventBus);
        HotpotPlacements.PLACEMENTS.register(modEventBus);
        HotpotPlacementRenderers.PLACEMENT_RENDERERS.register(modEventBus);
        HotpotSoupIngredients.CONDITIONS.register(modEventBus);
        HotpotSoupIngredients.ACTIONS.register(modEventBus);
        HotpotItemSpecialRenderers.ITEM_SPECIAL_RENDERERS.register(modEventBus);
        HotpotSpriteProcessors.SPRITE_PROCESSORS.register(modEventBus);
    }
}