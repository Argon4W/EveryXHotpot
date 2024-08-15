package com.github.argon4w.hotpot.client.items.sprites.colors;

import com.github.argon4w.hotpot.client.HotpotColor;
import com.github.argon4w.hotpot.items.sprites.IHotpotSpriteConfig;

public class HotpotEmptySpriteColorProvider implements IHotpotSpriteColorProvider {
    @Override
    public HotpotColor getColor(IHotpotSpriteConfig config) {
        return HotpotColor.WHITE;
    }
}
