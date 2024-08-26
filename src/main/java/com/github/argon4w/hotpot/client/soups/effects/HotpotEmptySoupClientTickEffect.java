package com.github.argon4w.hotpot.client.soups.effects;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;

public class HotpotEmptySoupClientTickEffect implements IHotpotSoupClientTickEffect {
    @Override
    public void tick(LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {

    }

    @Override
    public Holder<IHotpotSoupClientTickEffectSerializer<?>> getSerializerHolder() {
        return HotpotSoupClientTickEffects.EMPTY_SOUP_CLIENT_TICK_EFFECT_SERIALIZER;
    }

    public static class Serializer implements IHotpotSoupClientTickEffectSerializer<HotpotEmptySoupClientTickEffect> {
        public static final MapCodec<HotpotEmptySoupClientTickEffect> CODEC = MapCodec.unit(HotpotEmptySoupClientTickEffect::new);

        @Override
        public MapCodec<HotpotEmptySoupClientTickEffect> getCodec() {
            return CODEC;
        }
    }
}
