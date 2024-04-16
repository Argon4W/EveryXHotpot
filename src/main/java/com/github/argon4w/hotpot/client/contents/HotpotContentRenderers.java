package com.github.argon4w.hotpot.client.contents;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.client.contents.renderers.HotpotEmptyContentRenderer;
import com.github.argon4w.hotpot.client.contents.renderers.HotpotItemContentRenderer;
import com.github.argon4w.hotpot.client.contents.renderers.HotpotPlayerContentRenderer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class HotpotContentRenderers {
    public static final ResourceLocation EMPTY_CONTENT_RENDERER_LOCATION = new ResourceLocation(HotpotModEntry.MODID, "empty_content");

    public static final ResourceKey<Registry<IHotpotContentRenderer>> CONTENT_RENDERER_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(HotpotModEntry.MODID, "content_renderer"));
    public static final DeferredRegister<IHotpotContentRenderer> CONTENT_RENDERERS = DeferredRegister.create(CONTENT_RENDERER_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Supplier<IForgeRegistry<IHotpotContentRenderer>> CONTENT_RENDERER_REGISTRY = CONTENT_RENDERERS.makeRegistry(() -> new RegistryBuilder<IHotpotContentRenderer>().setDefaultKey(EMPTY_CONTENT_RENDERER_LOCATION));

    public static final RegistryObject<IHotpotContentRenderer> COOKING_RECIPE_CONTENT_RENDERER = CONTENT_RENDERERS.register("cooking_recipe_content", HotpotItemContentRenderer::new);
    public static final RegistryObject<IHotpotContentRenderer> SMELTING_RECIPE_CONTENT_RENDERER = CONTENT_RENDERERS.register("smelting_recipe_content", HotpotItemContentRenderer::new);
    public static final RegistryObject<IHotpotContentRenderer> PLAYER_CONTENT_RENDERER = CONTENT_RENDERERS.register("player_content", HotpotPlayerContentRenderer::new);
    public static final RegistryObject<IHotpotContentRenderer> EMPTY_CONTENT_RENDERER = CONTENT_RENDERERS.register("empty_content", HotpotEmptyContentRenderer::new);

    public static IHotpotContentRenderer getEmptyContentRenderer() {
        return EMPTY_CONTENT_RENDERER.get();
    }

    public static IForgeRegistry<IHotpotContentRenderer> getContentRendererRegistry() {
        return CONTENT_RENDERER_REGISTRY.get();
    }

    public static IHotpotContentRenderer getContentRenderer(ResourceLocation resourceLocation) {
        return getContentRendererRegistry().getValue(resourceLocation);
    }
}
