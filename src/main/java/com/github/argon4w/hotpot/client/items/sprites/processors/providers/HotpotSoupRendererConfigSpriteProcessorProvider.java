package com.github.argon4w.hotpot.client.items.sprites.processors.providers;

import com.github.argon4w.hotpot.api.client.items.sprites.processors.providers.IHotpotSpriteProcessorProvider;
import com.github.argon4w.hotpot.client.items.sprites.processors.HotpotSpriteProcessors;
import com.github.argon4w.hotpot.client.soups.HotpotSoupRendererConfigManager;
import com.github.argon4w.hotpot.client.soups.HotpotSoupSpriteConfig;
import com.github.argon4w.hotpot.items.sprites.HotpotSoupRendererConfigSpriteConfig;
import com.github.argon4w.hotpot.api.items.sprites.IHotpotSpriteConfig;
import net.minecraft.resources.ResourceLocation;

public class HotpotSoupRendererConfigSpriteProcessorProvider implements IHotpotSpriteProcessorProvider {
    @Override
    public ResourceLocation getProcessorResourceLocation(IHotpotSpriteConfig config) {
        return config instanceof HotpotSoupRendererConfigSpriteConfig rendererConfig ? HotpotSoupRendererConfigManager.getSoupRendererConfig(rendererConfig.soupRendererConfigResourceLocation()).spriteConfig().map(HotpotSoupSpriteConfig::processorResourceLocation).orElse(HotpotSpriteProcessors.EMPTY_SPRITE_PROCESSOR_LOCATION) : HotpotSpriteProcessors.EMPTY_SPRITE_PROCESSOR_LOCATION;
    }
}
