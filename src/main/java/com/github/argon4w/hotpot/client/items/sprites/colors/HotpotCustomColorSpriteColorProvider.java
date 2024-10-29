package com.github.argon4w.hotpot.client.items.sprites.colors;

import com.github.argon4w.hotpot.api.client.items.sprites.colors.IHotpotSpriteColorProvider;
import com.github.argon4w.hotpot.api.items.sprites.IHotpotSpriteConfig;
import com.github.argon4w.hotpot.client.HotpotColor;
import com.github.argon4w.hotpot.items.sprites.HotpotCustomColorSpriteConfig;

public class HotpotCustomColorSpriteColorProvider implements IHotpotSpriteColorProvider {
    @Override
    public HotpotColor getColor(IHotpotSpriteConfig config) {
        return config instanceof HotpotCustomColorSpriteConfig customColor ? customColor.color() : HotpotColor.WHITE;
    }
}
