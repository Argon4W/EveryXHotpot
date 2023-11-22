package com.github.argon4w.hotpot;

import com.github.argon4w.hotpot.blocks.HotpotBlock;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.blocks.HotpotPlaceableBlock;
import com.github.argon4w.hotpot.blocks.HotpotPlaceableBlockEntity;
import com.github.argon4w.hotpot.items.HotpotBlockEntityWithoutLevelRenderer;
import com.github.argon4w.hotpot.items.HotpotChopstickItem;
import com.github.argon4w.hotpot.items.HotpotPlaceableBlockItem;
import com.github.argon4w.hotpot.items.HotpotSpicePackItem;
import com.github.argon4w.hotpot.placeables.HotpotPlaceables;
import com.github.argon4w.hotpot.soups.effects.HotpotMobEffect;
import com.github.argon4w.hotpot.spices.HotpotSpicePackRecipe;
import com.mojang.datafixers.DSL;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.function.Function;

@Mod(HotpotModEntry.MODID)
public class HotpotModEntry {
    public static final String MODID = "everyxhotpot";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final ResourceLocation TAG_LOCATION = new ResourceLocation(HotpotModEntry.MODID, "hotpot_tags");

    public static final RegistryObject<Block> HOTPOT_BLOCK = HotpotRegistries.BLOCKS.register("hotpot", HotpotBlock::new);
    public static final RegistryObject<Block> HOTPOT_PLACEABLE = HotpotRegistries.BLOCKS.register("hotpot_plate", HotpotPlaceableBlock::new);
    public static final RegistryObject<BlockEntityType<HotpotBlockEntity>> HOTPOT_BLOCK_ENTITY = HotpotRegistries.BLOCK_ENTITY_TYPES.register("hotpot",
            () -> BlockEntityType.Builder.of(HotpotBlockEntity::new, HOTPOT_BLOCK.get()).build(DSL.remainderType()));
    public static final RegistryObject<BlockEntityType<HotpotPlaceableBlockEntity>> HOTPOT_PLACEABLE_BLOCK_ENTITY = HotpotRegistries.BLOCK_ENTITY_TYPES.register("hotpot_placeable",
            () -> BlockEntityType.Builder.of(HotpotPlaceableBlockEntity::new, HOTPOT_PLACEABLE.get()).build(DSL.remainderType()));

    public static final RegistryObject<Item> HOTPOT_BLOCK_ITEM = HotpotRegistries.ITEMS.register("hotpot", () -> new BlockItem(HOTPOT_BLOCK.get(), new Item.Properties().tab(HotpotModEntry.HOTPOT_CREATIVE_TAB)));
    public static final RegistryObject<Item> HOTPOT_SMALL_PLATE_BLOCK_ITEM = HotpotRegistries.ITEMS.register("hotpot_small_plate", () -> new HotpotPlaceableBlockItem(HotpotPlaceables.getPlaceableOrElseEmpty("SmallPlate")));
    public static final RegistryObject<Item> HOTPOT_LONG_PLATE_BLOCK_ITEM = HotpotRegistries.ITEMS.register("hotpot_long_plate", () -> new HotpotPlaceableBlockItem(HotpotPlaceables.getPlaceableOrElseEmpty("LongPlate")));
    public static final RegistryObject<Item> HOTPOT_CHOPSTICK = HotpotRegistries.ITEMS.register("hotpot_chopstick", HotpotChopstickItem::new);
    public static final RegistryObject<Item> HOTPOT_SPICE_PACK = HotpotRegistries.ITEMS.register("hotpot_spice_pack", HotpotSpicePackItem::new);
    public static final RegistryObject<MobEffect> HOTPOT_WARM = HotpotRegistries.MOB_EFFECTS.register("warm", () -> new HotpotMobEffect(MobEffectCategory.BENEFICIAL, (240 << 16) | (240 << 8) | 240));
    public static final RegistryObject<MobEffect> HOTPOT_ACRID = HotpotRegistries.MOB_EFFECTS.register("acrid", () -> new HotpotMobEffect(MobEffectCategory.BENEFICIAL, (240 << 16) | (84 << 8) | 64).addAttributeModifier(Attributes.ATTACK_SPEED, "46f33e49-ce96-4c75-b126-60a1e4117a8f", 0.5f, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final RegistryObject<RecipeSerializer<HotpotSpicePackRecipe>> HOTPOT_SPICE_PACK_SPECIAL_RECIPE = HotpotRegistries.RECIPE_SERIALIZERS.register("crafting_special_hotpot_spice_pack", () -> new SimpleRecipeSerializer<>(HotpotSpicePackRecipe::new));

    public static HotpotBlockEntityWithoutLevelRenderer HOTPOT_BEWLR;

    public static final DamageSource HOTPOT_DAMAGE_SOURCE = (new DamageSource("in_hotpot")).setIsFire();

    public static final CreativeModeTab HOTPOT_CREATIVE_TAB = new CreativeModeTab("EveryXHotpot") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(HotpotModEntry.HOTPOT_BLOCK_ITEM.get());
        }
    };

    public HotpotModEntry() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        HotpotRegistries.BLOCKS.register(modEventBus);
        HotpotRegistries.ITEMS.register(modEventBus);
        HotpotRegistries.BLOCK_ENTITY_TYPES.register(modEventBus);
        HotpotRegistries.RECIPE_SERIALIZERS.register(modEventBus);
        HotpotRegistries.MOB_EFFECTS.register(modEventBus);
    }
}