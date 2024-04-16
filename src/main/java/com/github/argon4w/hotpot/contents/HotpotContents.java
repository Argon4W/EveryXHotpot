package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.HotpotModEntry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class HotpotContents {
    public static final ResourceLocation EMPTY_CONTENT_LOCATION = new ResourceLocation(HotpotModEntry.MODID, "empty_content");

    public static final ResourceKey<Registry<IHotpotContentFactory<?>>> CONTENT_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(HotpotModEntry.MODID, "content"));
    public static final DeferredRegister<IHotpotContentFactory<?>> CONTENTS = DeferredRegister.create(CONTENT_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Supplier<IForgeRegistry<IHotpotContentFactory<?>>> CONTENT_REGISTRY = CONTENTS.makeRegistry(() -> new RegistryBuilder<IHotpotContentFactory<?>>().setDefaultKey(EMPTY_CONTENT_LOCATION));

    public static final RegistryObject<IHotpotContentFactory<HotpotCookingRecipeContent>> COOKING_RECIPE_CONTENT = CONTENTS.register("cooking_recipe_content", () -> HotpotCookingRecipeContent::new);
    public static final RegistryObject<IHotpotContentFactory<HotpotSmeltingRecipeContent>> SMELTING_RECIPE_CONTENT = CONTENTS.register("smelting_recipe_content", () -> HotpotSmeltingRecipeContent::new);
    public static final RegistryObject<IHotpotContentFactory<HotpotPlayerContent>> PLAYER_CONTENT = CONTENTS.register("player_content", () -> HotpotPlayerContent::new);
    public static final RegistryObject<IHotpotContentFactory<HotpotEmptyContent>> EMPTY_CONTENT = CONTENTS.register("empty_content", () -> HotpotEmptyContent::new);

    public static IHotpotContentFactory<HotpotEmptyContent> getEmptyContent() {
        return EMPTY_CONTENT.get();
    }

    public static IForgeRegistry<IHotpotContentFactory<?>> getContentRegistry() {
        return CONTENT_REGISTRY.get();
    }

    public static IHotpotContentFactory<?> getContentFactory(ResourceLocation resourceLocation) {
        return getContentRegistry().getValue(resourceLocation);
    }
}
