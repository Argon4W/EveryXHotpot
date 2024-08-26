package com.github.argon4w.hotpot.soups.components.contents;

import com.github.argon4w.hotpot.IHotpotResult;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.codecs.LazyMapCodec;
import com.github.argon4w.hotpot.contents.AbstractHotpotItemStackContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.items.components.HotpotSpriteConfigDataComponent;
import com.github.argon4w.hotpot.items.sprites.HotpotSpriteConfigSerializers;
import com.github.argon4w.hotpot.items.sprites.IHotpotSpriteConfig;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.components.AbstractHotpotSoupComponent;
import com.github.argon4w.hotpot.soups.components.HotpotSoupComponentTypeSerializers;
import com.github.argon4w.hotpot.soups.components.IHotpotSoupComponentType;
import com.github.argon4w.hotpot.soups.components.IHotpotSoupComponentTypeSerializer;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.List;

public class HotpotApplySpriteConfigsWhenGetContentSoupComponent extends AbstractHotpotSoupComponent {
    private final List<IHotpotSpriteConfig> spriteConfigs;

    public HotpotApplySpriteConfigsWhenGetContentSoupComponent(List<IHotpotSpriteConfig> spriteConfigs) {
        this.spriteConfigs = spriteConfigs;
    }

    @Override
    public IHotpotResult<IHotpotContent> getContentResultByTableware(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<IHotpotContent> result) {
        if (result.isEmpty()) {
            return result;
        }

        if (!(result.get() instanceof AbstractHotpotItemStackContent itemStackContent)) {
            return result;
        }

        itemStackContent.updateItemStack(itemStack -> HotpotSpriteConfigDataComponent.addSpriteConfigs(itemStack, spriteConfigs));
        return result;
    }

    public static class Type implements IHotpotSoupComponentType<HotpotApplySpriteConfigsWhenGetContentSoupComponent> {
        private final List<IHotpotSpriteConfig> spriteConfigs;
        private final HotpotApplySpriteConfigsWhenGetContentSoupComponent unit;

        private final MapCodec<HotpotApplySpriteConfigsWhenGetContentSoupComponent> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, HotpotApplySpriteConfigsWhenGetContentSoupComponent> streamCodec;

        public Type(List<IHotpotSpriteConfig> spriteConfigs) {
            this.spriteConfigs = spriteConfigs;
            this.unit = new HotpotApplySpriteConfigsWhenGetContentSoupComponent(spriteConfigs);

            this.codec = MapCodec.unit(unit);
            this.streamCodec = StreamCodec.unit(unit);
        }

        @Override
        public HotpotApplySpriteConfigsWhenGetContentSoupComponent get() {
            return unit;
        }

        @Override
        public MapCodec<HotpotApplySpriteConfigsWhenGetContentSoupComponent> getCodec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotApplySpriteConfigsWhenGetContentSoupComponent> getStreamCodec() {
            return streamCodec;
        }

        @Override
        public Holder<IHotpotSoupComponentTypeSerializer<?>> getSerializerHolder() {
            return HotpotSoupComponentTypeSerializers.APPLY_SPRITE_CONFIGS_WHEN_GET_CONTENT_SOUP_COMPONENT_TYPE_SERIALIZER;
        }

        public List<IHotpotSpriteConfig> getSpriteConfigs() {
            return spriteConfigs;
        }
    }

    public static class Serializer implements IHotpotSoupComponentTypeSerializer<HotpotApplySpriteConfigsWhenGetContentSoupComponent> {
        public static final MapCodec<Type> CODEC = LazyMapCodec.of(() -> HotpotSpriteConfigSerializers.CODEC.listOf().fieldOf("sprite_configs").xmap(Type::new, Type::getSpriteConfigs));
        public static final StreamCodec<RegistryFriendlyByteBuf, Type> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> HotpotSpriteConfigSerializers.STREAM_CODEC.apply(ByteBufCodecs.list()).map(Type::new, Type::getSpriteConfigs));

        @Override
        public MapCodec<? extends IHotpotSoupComponentType<HotpotApplySpriteConfigsWhenGetContentSoupComponent>> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupComponentType<HotpotApplySpriteConfigsWhenGetContentSoupComponent>> getStreamCodec() {
            return STREAM_CODEC;
        }
    }
}
