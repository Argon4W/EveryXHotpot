package com.github.argon4w.hotpot;

import com.github.argon4w.hotpot.blocks.HotpotBlock;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.blocks.HotpotPlaceableBlock;
import com.github.argon4w.hotpot.blocks.HotpotPlaceableBlockEntity;
import com.github.argon4w.hotpot.items.HotpotBlockEntityWithoutLevelRenderer;
import com.github.argon4w.hotpot.items.HotpotChopstickItem;
import com.github.argon4w.hotpot.items.HotpotPlaceableBlockItem;
import com.mojang.datafixers.DSL;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
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

    public static final RegistryObject<Block> HOTPOT_BLOCK = HotpotRegistries.BLOCKS.register("hotpot", HotpotBlock::new);
    public static final RegistryObject<Block> HOTPOT_PLACEABLE = HotpotRegistries.BLOCKS.register("hotpot_plate", HotpotPlaceableBlock::new);
    public static final RegistryObject<BlockEntityType<HotpotBlockEntity>> HOTPOT_BLOCK_ENTITY = HotpotRegistries.BLOCK_ENTITY_TYPES.register("hotpot",
            () -> BlockEntityType.Builder.of(HotpotBlockEntity::new, HOTPOT_BLOCK.get()).build(DSL.remainderType()));
    public static final RegistryObject<BlockEntityType<HotpotPlaceableBlockEntity>> HOTPOT_PLACEABLE_BLOCK_ENTITY = HotpotRegistries.BLOCK_ENTITY_TYPES.register("hotpot_placeable",
            () -> BlockEntityType.Builder.of(HotpotPlaceableBlockEntity::new, HOTPOT_PLACEABLE.get()).build(DSL.remainderType()));

    public static final RegistryObject<Item> HOTPOT_BLOCK_ITEM = HotpotRegistries.ITEMS.register("hotpot", () -> new BlockItem(HOTPOT_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> HOTPOT_SMALL_PLATE_BLOCK_ITEM = HotpotRegistries.ITEMS.register("hotpot_small_plate", () -> new HotpotPlaceableBlockItem(HotpotDefinitions.getPlaceableOrElseEmpty("SmallPlate")));
    public static final RegistryObject<Item> HOTPOT_LONG_PLATE_BLOCK_ITEM = HotpotRegistries.ITEMS.register("hotpot_long_plate", () -> new HotpotPlaceableBlockItem(HotpotDefinitions.getPlaceableOrElseEmpty("LongPlate")));
    public static final RegistryObject<Item> HOTPOT_CHOPSTICK = HotpotRegistries.ITEMS.register("hotpot_chopstick", HotpotChopstickItem::new);
    public static final RegistryObject<CreativeModeTab> EVERY_X_HOTPOT_TAB = HotpotRegistries.CREATIVE_MODE_TABS.register("every_x_hotpot_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> HOTPOT_BLOCK_ITEM.get().getDefaultInstance())
            .title(Component.translatable("itemGroup.EveryXHotpot"))
            .displayItems((parameters, output) -> {
                output.accept(HOTPOT_BLOCK_ITEM.get());
                output.accept(HOTPOT_CHOPSTICK.get());
                output.accept(HOTPOT_SMALL_PLATE_BLOCK_ITEM.get());
                output.accept(HOTPOT_LONG_PLATE_BLOCK_ITEM.get());
            }).build());

    public static final HotpotBlockEntityWithoutLevelRenderer HOTPOT_BEWLR = new HotpotBlockEntityWithoutLevelRenderer();

    public static final ResourceKey<DamageType> IN_HOTPOT_DAMAGE_KEY = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID, "in_hotpot"));
    public static final Function<Level, Holder<DamageType>> IN_HOTPOT_DAMAGE_TYPE = level -> level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(IN_HOTPOT_DAMAGE_KEY);

    public HotpotModEntry() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        HotpotRegistries.BLOCKS.register(modEventBus);
        HotpotRegistries.ITEMS.register(modEventBus);
        HotpotRegistries.CREATIVE_MODE_TABS.register(modEventBus);
        HotpotRegistries.BLOCK_ENTITY_TYPES.register(modEventBus);
    }
}