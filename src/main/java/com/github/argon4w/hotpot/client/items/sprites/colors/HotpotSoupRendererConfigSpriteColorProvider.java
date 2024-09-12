package com.github.argon4w.hotpot.client.items.sprites.colors;

import com.github.argon4w.hotpot.client.HotpotColor;
import com.github.argon4w.hotpot.client.soups.HotpotSoupRendererConfigManager;
import com.github.argon4w.hotpot.client.soups.HotpotSoupSpriteConfig;
import com.github.argon4w.hotpot.items.sprites.HotpotSoupRendererConfigSpriteConfig;
import com.github.argon4w.hotpot.items.sprites.IHotpotSpriteConfig;

public class HotpotSoupRendererConfigSpriteColorProvider implements IHotpotSpriteColorProvider {
    @Override
    public HotpotColor getColor(IHotpotSpriteConfig config) {
        return config instanceof HotpotSoupRendererConfigSpriteConfig rendererConfig ? HotpotSoupRendererConfigManager.getSoupRendererConfig(rendererConfig.soupRendererConfigResourceLocation()).spriteConfig().map(HotpotSoupSpriteConfig::color).orElse(HotpotColor.WHITE) : HotpotColor.WHITE;
    }
}
