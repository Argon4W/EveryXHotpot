package com.github.argon4w.hotpot.client.contents;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.client.contents.renderers.items.HotpotDefaultItemContentRenderer;
import com.github.argon4w.hotpot.client.contents.renderers.items.HotpotSkewerItemContentRenderer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class HotpotItemContentSpecialRenderers {
    public static final ResourceLocation DEFAULT_ITEM_CONTENT_RENDERER_LOCATION = new ResourceLocation(HotpotModEntry.MODID, "default_item_content");

    public static final ResourceKey<Registry<IHotpotItemContentSpecialRenderer>> ITEM_CONTENT_SPECIAL_RENDERER_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(HotpotModEntry.MODID, "item_content_special_renderer"));
    public static final DeferredRegister<IHotpotItemContentSpecialRenderer> ITEM_CONTENT_SPECIAL_RENDERERS = DeferredRegister.create(ITEM_CONTENT_SPECIAL_RENDERER_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Supplier<IForgeRegistry<IHotpotItemContentSpecialRenderer>> ITEM_CONTENT_SPECIAL_RENDERER_REGISTRY = ITEM_CONTENT_SPECIAL_RENDERERS.makeRegistry(() -> new RegistryBuilder<IHotpotItemContentSpecialRenderer>().setDefaultKey(DEFAULT_ITEM_CONTENT_RENDERER_LOCATION));

    public static final RegistryObject<IHotpotItemContentSpecialRenderer> DEFAULT_ITEM_CONTENT_RENDERER = ITEM_CONTENT_SPECIAL_RENDERERS.register("default_item_content", HotpotDefaultItemContentRenderer::new);
    public static final RegistryObject<IHotpotItemContentSpecialRenderer> SKEWER_ITEM_CONTENT_RENDERER = ITEM_CONTENT_SPECIAL_RENDERERS.register("hotpot_skewer", HotpotSkewerItemContentRenderer::new);

    public static IHotpotItemContentSpecialRenderer getDefaultItemContentRenderer() {
        return DEFAULT_ITEM_CONTENT_RENDERER.get();
    }

    public static IForgeRegistry<IHotpotItemContentSpecialRenderer> getItemContentSpecialRendererRegistry() {
        return ITEM_CONTENT_SPECIAL_RENDERER_REGISTRY.get();
    }

    public static IHotpotItemContentSpecialRenderer getItemContentSpecialRenderer(ResourceLocation resourceLocation) {
        return getItemContentSpecialRendererRegistry().getValue(resourceLocation);
    }
}
