package com.github.argon4w.hotpot.client.soups.effects;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import net.minecraft.core.Holder;

public interface IHotpotSoupClientTickEffect {
    void tick(LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity);
    Holder<IHotpotSoupClientTickEffectSerializer<?>> getSerializerHolder();
}
