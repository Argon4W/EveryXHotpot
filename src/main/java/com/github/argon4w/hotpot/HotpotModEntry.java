package com.github.argon4w.hotpot;

import com.github.argon4w.hotpot.blocks.HotpotBlock;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.blocks.HotpotPlacementBlock;
import com.github.argon4w.hotpot.blocks.HotpotPlacementBlockEntity;
import com.github.argon4w.hotpot.client.contents.HotpotContentRenderers;
import com.github.argon4w.hotpot.client.contents.HotpotItemContentSpecialRenderers;
import com.github.argon4w.hotpot.client.items.HotpotItemSpecialRenderers;
import com.github.argon4w.hotpot.client.items.process.HotpotSpriteProcessors;
import com.github.argon4w.hotpot.client.placements.HotpotPlacementRenderers;
import com.github.argon4w.hotpot.client.soups.HotpotSoupCustomElements;
import com.github.argon4w.hotpot.client.soups.HotpotSoupRendererConfigManager;
import com.github.argon4w.hotpot.contents.HotpotContents;
import com.github.argon4w.hotpot.client.items.HotpotBlockEntityWithoutLevelRenderer;
import com.github.argon4w.hotpot.items.*;
import com.github.argon4w.hotpot.network.HotpotUpdateSoupFactoriesPacket;
import com.github.argon4w.hotpot.placements.HotpotPlacements;
import com.github.argon4w.hotpot.recipes.HotpotSkewerRecipe;
import com.github.argon4w.hotpot.soups.HotpotSoupFactoryManager;
import com.github.argon4w.hotpot.soups.HotpotSoupTypes;
import com.github.argon4w.hotpot.soups.effects.HotpotMobEffect;
import com.github.argon4w.hotpot.soups.recipes.HotpotCookingRecipe;
import com.github.argon4w.hotpot.soups.recipes.HotpotSoupBaseRecipe;
import com.github.argon4w.hotpot.soups.recipes.HotpotSoupIngredientRecipe;
import com.github.argon4w.hotpot.soups.recipes.HotpotSoupRechargeRecipe;
import com.github.argon4w.hotpot.soups.recipes.ingredients.HotpotSoupIngredients;
import com.github.argon4w.hotpot.recipes.HotpotSpicePackRecipe;
import com.mojang.datafixers.DSL;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Holder;
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
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.function.Function;

@Mod(HotpotModEntry.MODID)
public class HotpotModEntry {
    public static final String MODID = "everyxhotpot";
    public static final String HOTPOT_NETWORK_PROTOCOL_VERSION = "2";
    public static final int HOTPOT_SPRITE_TINT_INDEX = 230419;

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final ResourceLocation TAG_LOCATION = new ResourceLocation(HotpotModEntry.MODID, "hotpot_tags");

    public static final RegistryObject<Block> HOTPOT_BLOCK = HotpotRegistries.BLOCKS.register("hotpot", HotpotBlock::new);
    public static final RegistryObject<Block> HOTPOT_PLACEMENT = HotpotRegistries.BLOCKS.register("hotpot_placement", HotpotPlacementBlock::new);
    public static final RegistryObject<BlockEntityType<HotpotBlockEntity>> HOTPOT_BLOCK_ENTITY = HotpotRegistries.BLOCK_ENTITY_TYPES.register("hotpot",
            () -> BlockEntityType.Builder.of(HotpotBlockEntity::new, HOTPOT_BLOCK.get()).build(DSL.remainderType()));
    public static final RegistryObject<BlockEntityType<HotpotPlacementBlockEntity>> HOTPOT_PLACEMENT_BLOCK_ENTITY = HotpotRegistries.BLOCK_ENTITY_TYPES.register("hotpot_placement",
            () -> BlockEntityType.Builder.of(HotpotPlacementBlockEntity::new, HOTPOT_PLACEMENT.get()).build(DSL.remainderType()));

    public static final RegistryObject<Item> HOTPOT_PAPER_BOWL = HotpotRegistries.ITEMS.register("hotpot_paper_bowl", HotpotPaperBowlItem::new);
    public static final RegistryObject<Item> HOTPOT_SKEWER = HotpotRegistries.ITEMS.register("hotpot_skewer", HotpotSkewerItem::new);
    public static final RegistryObject<Item> HOTPOT_SLOTTED_SPOON = HotpotRegistries.ITEMS.register("hotpot_slotted_spoon", () -> new HotpotSpoonItem(true));
    public static final RegistryObject<Item> HOTPOT_SOUP_SPOON = HotpotRegistries.ITEMS.register("hotpot_soup_spoon", () -> new HotpotSpoonItem(false));
    public static final RegistryObject<Item> HOTPOT_BLOCK_ITEM = HotpotRegistries.ITEMS.register("hotpot", () -> new BlockItem(HOTPOT_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> HOTPOT_SMALL_PLATE_BLOCK_ITEM = HotpotRegistries.ITEMS.register("hotpot_small_plate", () -> new HotpotPlacementBlockItem(() -> HotpotPlacements.SMALL_PLATE.get().build()));
    public static final RegistryObject<Item> HOTPOT_LONG_PLATE_BLOCK_ITEM = HotpotRegistries.ITEMS.register("hotpot_long_plate", () -> new HotpotPlacementBlockItem(() -> HotpotPlacements.LONG_PLATE.get().build()));
    public static final RegistryObject<Item> HOTPOT_LARGE_ROUND_PLATE_BLOCK_ITEM = HotpotRegistries.ITEMS.register("hotpot_large_round_plate", () -> new HotpotPlacementBlockItem(() -> HotpotPlacements.LARGE_ROUND_PLATE.get().build()));
    public static final RegistryObject<Item> HOTPOT_CHOPSTICK = HotpotRegistries.ITEMS.register("hotpot_chopstick", HotpotChopstickItem::new);
    public static final RegistryObject<Item> HOTPOT_SPICE_PACK = HotpotRegistries.ITEMS.register("hotpot_spice_pack", HotpotSpicePackItem::new);
    public static final RegistryObject<MobEffect> HOTPOT_WARM = HotpotRegistries.MOB_EFFECTS.register("warm", () -> new HotpotMobEffect(MobEffectCategory.BENEFICIAL, (240 << 16) | (240 << 8) | 240));
    public static final RegistryObject<MobEffect> HOTPOT_ACRID = HotpotRegistries.MOB_EFFECTS.register("acrid", () -> new HotpotMobEffect(MobEffectCategory.BENEFICIAL, (240 << 16) | (84 << 8) | 64).addAttributeModifier(Attributes.ATTACK_SPEED, "46f33e49-ce96-4c75-b126-60a1e4117a8f", 0.5f, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final RegistryObject<RecipeSerializer<HotpotSpicePackRecipe>> HOTPOT_SPICE_PACK_SPECIAL_RECIPE = HotpotRegistries.RECIPE_SERIALIZERS.register("crafting_special_hotpot_spice_pack", () -> new SimpleCraftingRecipeSerializer<>(HotpotSpicePackRecipe::new));
    public static final RegistryObject<RecipeSerializer<HotpotSkewerRecipe>> HOTPOT_SKEWER_SPECIAL_RECIPE = HotpotRegistries.RECIPE_SERIALIZERS.register("crafting_special_hotpot_skewer", () -> new SimpleCraftingRecipeSerializer<>(HotpotSkewerRecipe::new));
    public static final RegistryObject<HotpotSoupIngredientRecipe.Serializer> HOTPOT_SOUP_INGREDIENT_RECIPE_SERIALIZER = HotpotRegistries.RECIPE_SERIALIZERS.register("hotpot_soup_ingredient_recipe", HotpotSoupIngredientRecipe.Serializer::new);
    public static final RegistryObject<HotpotSoupBaseRecipe.Serializer> HOTPOT_SOUP_BASE_RECIPE_SERIALIZER = HotpotRegistries.RECIPE_SERIALIZERS.register("hotpot_soup_base_recipe", HotpotSoupBaseRecipe.Serializer::new);
    public static final RegistryObject<HotpotSoupRechargeRecipe.Serializer> HOTPOT_SOUP_RECHARGE_RECIPE_SERIALIZER = HotpotRegistries.RECIPE_SERIALIZERS.register("hotpot_soup_recharge_recipe", HotpotSoupRechargeRecipe.Serializer::new);
    public static final RegistryObject<HotpotCookingRecipe.Serializer> HOTPOT_COOKING_RECIPE_SERIALIZER = HotpotRegistries.RECIPE_SERIALIZERS.register("hotpot_cooking_recipe", HotpotCookingRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<HotpotSoupIngredientRecipe>> HOTPOT_SOUP_INGREDIENT_RECIPE_TYPE = HotpotRegistries.RECIPE_TYPES.register("hotpot_soup_ingredient_recipe_type", () -> new RecipeType<>() {});
    public static final RegistryObject<RecipeType<HotpotSoupBaseRecipe>> HOTPOT_SOUP_BASE_RECIPE_TYPE = HotpotRegistries.RECIPE_TYPES.register("hotpot_soup_base_recipe_type", () -> new RecipeType<>() {});
    public static final RegistryObject<RecipeType<HotpotSoupRechargeRecipe>> HOTPOT_SOUP_RECHARGE_RECIPE_TYPE = HotpotRegistries.RECIPE_TYPES.register("hotpot_soup_recharge_recipe_type", () -> new RecipeType<>() {});
    public static final RegistryObject<RecipeType<HotpotCookingRecipe>> HOTPOT_COOKING_RECIPE = HotpotRegistries.RECIPE_TYPES.register("hotpot_cooking_recipe_type", () -> new RecipeType<>() {});

    public static final RegistryObject<CreativeModeTab> HOTPOT_TAB = HotpotRegistries.CREATIVE_MODE_TABS.register("every_x_hotpot_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(() -> HOTPOT_BLOCK_ITEM.get().getDefaultInstance())
            .title(Component.translatable("itemGroup.EveryXHotpot"))
            .displayItems((parameters, output) -> {
                output.accept(HOTPOT_BLOCK_ITEM.get());
                output.accept(HOTPOT_CHOPSTICK.get());
                output.accept(HOTPOT_SMALL_PLATE_BLOCK_ITEM.get());
                output.accept(HOTPOT_LONG_PLATE_BLOCK_ITEM.get());
                output.accept(HOTPOT_LARGE_ROUND_PLATE_BLOCK_ITEM.get());
                output.accept(HOTPOT_SPICE_PACK.get());
                output.accept(HOTPOT_SLOTTED_SPOON.get());
                output.accept(HOTPOT_SOUP_SPOON.get());
                output.accept(HOTPOT_PAPER_BOWL.get());
                output.accept(HOTPOT_SKEWER.get());
            }).build());

    public static final ResourceKey<DamageType> IN_HOTPOT_DAMAGE_KEY = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID, "in_hotpot"));
    public static final Function<Level, Holder<DamageType>> IN_HOTPOT_DAMAGE_TYPE = level -> level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(IN_HOTPOT_DAMAGE_KEY);

    public static HotpotBlockEntityWithoutLevelRenderer HOTPOT_SPECIAL_ITEM_RENDERER;
    public static HotpotSoupRendererConfigManager HOTPOT_SOUP_RENDERER_CONFIG_MANAGER;
    public static HotpotSoupFactoryManager HOTPOT_SOUP_FACTORY_MANAGER;

    public static final SimpleChannel HOTPOT_NETWORK_CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(HotpotModEntry.MODID, "network"),
            () -> HotpotModEntry.HOTPOT_NETWORK_PROTOCOL_VERSION,
            HotpotModEntry.HOTPOT_NETWORK_PROTOCOL_VERSION::equals,
            HotpotModEntry.HOTPOT_NETWORK_PROTOCOL_VERSION::equals
    );

    public HotpotModEntry() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        HotpotRegistries.BLOCKS.register(modEventBus);
        HotpotRegistries.ITEMS.register(modEventBus);
        HotpotRegistries.CREATIVE_MODE_TABS.register(modEventBus);
        HotpotRegistries.BLOCK_ENTITY_TYPES.register(modEventBus);
        HotpotRegistries.RECIPE_SERIALIZERS.register(modEventBus);
        HotpotRegistries.RECIPE_TYPES.register(modEventBus);
        HotpotRegistries.MOB_EFFECTS.register(modEventBus);

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

        HOTPOT_NETWORK_CHANNEL.messageBuilder(HotpotUpdateSoupFactoriesPacket.class, 0)
                .encoder(HotpotUpdateSoupFactoriesPacket::encoder)
                .decoder(HotpotUpdateSoupFactoriesPacket::decoder)
                .consumerMainThread(HotpotUpdateSoupFactoriesPacket::handler)
                .add();
    }
}