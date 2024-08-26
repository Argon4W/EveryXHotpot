package com.github.argon4w.hotpot.soups.components.updates;

import com.github.argon4w.hotpot.IHotpotResult;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.AbstractHotpotItemStackContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.items.components.HotpotFoodEffectsDataComponent;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.components.AbstractHotpotSoupComponent;
import com.github.argon4w.hotpot.soups.components.HotpotSoupComponentTypeSerializers;
import com.github.argon4w.hotpot.soups.components.IHotpotSoupComponentType;
import com.github.argon4w.hotpot.soups.components.IHotpotSoupComponentTypeSerializer;
import com.github.argon4w.hotpot.soups.components.containers.IHotpotMobEffectContainerSoupComponent;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class HotpotApplyMobEffectOnContentUpdateSoupComponent extends AbstractHotpotSoupComponent {
    private final List<ResourceLocation> keys;

    public HotpotApplyMobEffectOnContentUpdateSoupComponent(List<ResourceLocation> keys) {
        this.keys = keys;
    }

    @Override
    public IHotpotResult<IHotpotContent> onContentUpdate(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<IHotpotContent> result) {
        if (result.isEmpty()) {
            return result;
        }

        if (!(result.get() instanceof AbstractHotpotItemStackContent itemStackContent)) {
            return result;
        }

        if (itemStackContent.getCookingTime() >= 0) {
            return result;
        }

        itemStackContent.updateItemStack(itemStack -> {
            if (!itemStack.has(DataComponents.FOOD)) {
                return;
            }

            if (HotpotFoodEffectsDataComponent.hasDataComponent(itemStack)) {
                return;
            }

            soup.getComponentPairsByTypes(List.of(HotpotSoupComponentTypeSerializers.FIXED_MOB_EFFECT_CONTAINER_SOUP_COMPONENT_TYPE_SERIALIZER, HotpotSoupComponentTypeSerializers.DYNAMIC_MOB_EFFECT_CONTAINER_SOUP_COMPONENT_TYPE_SERIALIZER)).stream().filter(pair -> keys.isEmpty() || keys.contains(pair.getFirst())).map(Pair::getSecond).map(IHotpotMobEffectContainerSoupComponent::getMobEffectMap).forEach(mobEffectMap -> HotpotFoodEffectsDataComponent.addEffects(itemStack, mobEffectMap));
        });

        return result;
    }

    public static class Type implements IHotpotSoupComponentType<HotpotApplyMobEffectOnContentUpdateSoupComponent> {
        private final List<ResourceLocation> keys;
        private final HotpotApplyMobEffectOnContentUpdateSoupComponent unit;

        private final MapCodec<HotpotApplyMobEffectOnContentUpdateSoupComponent> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, HotpotApplyMobEffectOnContentUpdateSoupComponent> streamCodec;

        public Type(List<ResourceLocation> keys) {
            this.keys = keys;
            this.unit = new HotpotApplyMobEffectOnContentUpdateSoupComponent(keys);

            this.codec = MapCodec.unit(unit);
            this.streamCodec = StreamCodec.unit(unit);
        }

        @Override
        public MapCodec<HotpotApplyMobEffectOnContentUpdateSoupComponent> getCodec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotApplyMobEffectOnContentUpdateSoupComponent> getStreamCodec() {
            return streamCodec;
        }

        @Override
        public HotpotApplyMobEffectOnContentUpdateSoupComponent get() {
            return unit;
        }

        @Override
        public Holder<IHotpotSoupComponentTypeSerializer<?>> getSerializerHolder() {
            return HotpotSoupComponentTypeSerializers.APPLY_MOB_EFFECT_ON_CONTENT_UPDATE_SOUP_COMPONENT_TYPE_SERIALIZER;
        }

        public List<ResourceLocation> getKeys() {
            return keys;
        }
    }

    public static class Serializer implements IHotpotSoupComponentTypeSerializer<HotpotApplyMobEffectOnContentUpdateSoupComponent> {
        public static final MapCodec<Type> CODEC = ResourceLocation.CODEC.listOf().optionalFieldOf("keys", List.of()).xmap(Type::new, Type::getKeys);
        public static final StreamCodec<RegistryFriendlyByteBuf, Type> STREAM_CODEC = ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()).<RegistryFriendlyByteBuf>cast().map(Type::new, Type::getKeys);

        @Override
        public MapCodec<? extends IHotpotSoupComponentType<HotpotApplyMobEffectOnContentUpdateSoupComponent>> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupComponentType<HotpotApplyMobEffectOnContentUpdateSoupComponent>> getStreamCodec() {
            return STREAM_CODEC;
        }
    }
}
