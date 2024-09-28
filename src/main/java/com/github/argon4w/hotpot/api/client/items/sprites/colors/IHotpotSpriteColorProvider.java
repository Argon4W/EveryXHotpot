package com.github.argon4w.hotpot.api.client.items.sprites.colors;

import com.github.argon4w.hotpot.client.HotpotColor;
import com.github.argon4w.hotpot.api.items.sprites.IHotpotSpriteConfig;

public interface IHotpotSpriteColorProvider {
    HotpotColor getColor(IHotpotSpriteConfig config);
}
