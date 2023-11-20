package com.github.argon4w.hotpot;

import com.github.argon4w.hotpot.blocks.HotpotBlock;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.blocks.HotpotPlaceableBlock;
import com.github.argon4w.hotpot.blocks.HotpotPlaceableBlockEntity;
import com.github.argon4w.hotpot.items.*;
import com.github.argon4w.hotpot.placeables.HotpotPlaceables;
import com.github.argon4w.hotpot.soups.effects.HotpotMobEffect;
import com.github.argon4w.hotpot.spices.HotpotSpicePackRecipe;
import com.mojang.datafixers.DSL;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


@Mod(HotpotModEntry.MODID)
public class HotpotModEntry {
    public static final String MODID = "everyxhotpot";
    public static final ResourceLocation TAG_LOCATION = new ResourceLocation(HotpotModEntry.MODID, "hotpot_tags");
    public static final HotpotItemGroup HOTPOT_ITEM_GROUP = new HotpotItemGroup();

    public static final RegistryObject<Block> HOTPOT_BLOCK = HotpotRegistries.BLOCKS.register("hotpot", HotpotBlock::new);
    public static final RegistryObject<Block> HOTPOT_PLACEABLE = HotpotRegistries.BLOCKS.register("hotpot_plate", HotpotPlaceableBlock::new);
    public static final RegistryObject<TileEntityType<HotpotBlockEntity>> HOTPOT_BLOCK_ENTITY = HotpotRegistries.BLOCK_ENTITY_TYPES.register("hotpot",
            () -> TileEntityType.Builder.of(HotpotBlockEntity::new, HOTPOT_BLOCK.get()).build(DSL.remainderType()));
    public static final RegistryObject<TileEntityType<HotpotPlaceableBlockEntity>> HOTPOT_PLACEABLE_BLOCK_ENTITY = HotpotRegistries.BLOCK_ENTITY_TYPES.register("hotpot_placeable",
            () -> TileEntityType.Builder.of(HotpotPlaceableBlockEntity::new, HOTPOT_PLACEABLE.get()).build(DSL.remainderType()));

    public static final RegistryObject<Item> HOTPOT_BLOCK_ITEM = HotpotRegistries.ITEMS.register("hotpot", () -> new BlockItem(HOTPOT_BLOCK.get(), new Item.Properties().tab(HotpotModEntry.HOTPOT_ITEM_GROUP)));
    public static final RegistryObject<Item> HOTPOT_SMALL_PLATE_BLOCK_ITEM = HotpotRegistries.ITEMS.register("hotpot_small_plate", HotpotSmallPlateItem::new);
    public static final RegistryObject<Item> HOTPOT_LONG_PLATE_BLOCK_ITEM = HotpotRegistries.ITEMS.register("hotpot_long_plate", HotpotLongPlateItem::new);
    public static final RegistryObject<Item> HOTPOT_CHOPSTICK = HotpotRegistries.ITEMS.register("hotpot_chopstick", HotpotChopstickItem::new);
    public static final RegistryObject<Item> HOTPOT_SPICE_PACK = HotpotRegistries.ITEMS.register("hotpot_spice_pack", HotpotSpicePackItem::new);
    public static final RegistryObject<Effect> HOTPOT_WARM = HotpotRegistries.MOB_EFFECTS.register("warm", () -> new HotpotMobEffect(EffectType.BENEFICIAL, (240 << 16) | (240 << 8) | 240));
    public static final RegistryObject<Effect> HOTPOT_ACRID = HotpotRegistries.MOB_EFFECTS.register("acrid", () -> new HotpotMobEffect(EffectType.BENEFICIAL, (240 << 16) | (84 << 8) | 64).addAttributeModifier(Attributes.ATTACK_SPEED, "46f33e49-ce96-4c75-b126-60a1e4117a8f", 0.5f, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final RegistryObject<IRecipeSerializer<HotpotSpicePackRecipe>> HOTPOT_SPICE_PACK_SPECIAL_RECIPE = HotpotRegistries.RECIPE_SERIALIZERS.register("crafting_special_hotpot_spice_pack", () -> new SpecialRecipeSerializer<>(HotpotSpicePackRecipe::new));

    public static final DamageSource HOTPOT_DAMAGE_SOURCE = (new DamageSource("in_hotpot")).setIsFire();

    public HotpotModEntry() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        HotpotRegistries.BLOCKS.register(modEventBus);
        HotpotRegistries.ITEMS.register(modEventBus);
        HotpotRegistries.BLOCK_ENTITY_TYPES.register(modEventBus);
        HotpotRegistries.RECIPE_SERIALIZERS.register(modEventBus);
        HotpotRegistries.MOB_EFFECTS.register(modEventBus);
    }
}