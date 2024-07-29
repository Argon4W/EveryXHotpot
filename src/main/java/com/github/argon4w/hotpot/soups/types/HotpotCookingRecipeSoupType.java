package com.github.argon4w.hotpot.soups.types;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LazyMapCodec;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.client.items.process.HotpotSpriteProcessors;
import com.github.argon4w.hotpot.client.items.process.IHotpotSpriteProcessor;
import com.github.argon4w.hotpot.contents.HotpotContents;
import com.github.argon4w.hotpot.contents.HotpotCookingRecipeContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.contents.IHotpotContentFactory;
import com.github.argon4w.hotpot.items.HotpotSkewerItem;
import com.github.argon4w.hotpot.items.components.HotpotFoodEffectsDataComponent;
import com.github.argon4w.hotpot.items.components.HotpotSoupDataComponent;
import com.github.argon4w.hotpot.items.components.HotpotSpriteProcessorDataComponent;
import com.github.argon4w.hotpot.soups.HotpotSoupTypeFactoryHolder;
import com.github.argon4w.hotpot.soups.HotpotSoupTypes;
import com.github.argon4w.hotpot.soups.IHotpotSoupFactorySerializer;
import com.github.argon4w.hotpot.soups.IHotpotSoupTypeFactory;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HotpotCookingRecipeSoupType extends AbstractHotpotFluidBasedSoupType {
    private final HotpotSoupTypeFactoryHolder<?> soupTypeFactoryHolder;
    private final float waterLevelDropRate;
    private final List<MobEffectInstance> savedEffects;
    private final Holder<IHotpotSpriteProcessor> spriteProcessor;

    public HotpotCookingRecipeSoupType(HotpotSoupTypeFactoryHolder<?> soupTypeFactoryHolder, float waterLevelDropRate, List<MobEffectInstance> savedEffects, Holder<IHotpotSpriteProcessor> spriteProcessor) {
        this.soupTypeFactoryHolder = soupTypeFactoryHolder;
        this.waterLevelDropRate = waterLevelDropRate;
        this.savedEffects = savedEffects;
        this.spriteProcessor = spriteProcessor;

        this.waterLevel = 0.0f;
        this.overflowWaterLevel = 0.0f;
        this.activeness = 0.0f;
    }

    public HotpotCookingRecipeSoupType(HotpotSoupTypeFactoryHolder<?> soupTypeFactoryHolder, float waterLevelDropRate, List<MobEffectInstance> savedEffects, Holder<IHotpotSpriteProcessor> spriteProcessor, float waterLevel, float overflowWaterLevel, float activeness) {
        this.soupTypeFactoryHolder = soupTypeFactoryHolder;
        this.waterLevelDropRate = waterLevelDropRate;
        this.savedEffects = savedEffects;
        this.spriteProcessor = spriteProcessor;

        this.waterLevel = waterLevel;
        this.overflowWaterLevel = overflowWaterLevel;
        this.activeness = activeness;
    }

    @Override
    public ItemStack takeOutContentViaTableware(IHotpotContent content, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        ItemStack result = super.takeOutContentViaTableware(content, itemStack, hotpotBlockEntity, pos);

        if (!(content instanceof HotpotCookingRecipeContent cookingRecipeContent)) {
            return result;
        }

        if (cookingRecipeContent.getCookingTime() >= 0) {
            return result;
        }

        if (result.is(HotpotModEntry.HOTPOT_SKEWER.get())) {
            return HotpotSkewerItem.applyToSkewerItemStacks(result, this::apply);
        }

        return apply(result);
    }

    public ItemStack apply(ItemStack itemStack) {
        if (!itemStack.has(DataComponents.FOOD)) {
            return itemStack;
        }

        if (HotpotSoupDataComponent.hasDataComponent(itemStack)) {
            return itemStack;
        }

        HotpotSoupDataComponent.setSoup(itemStack, this);
        HotpotFoodEffectsDataComponent.addEffects(itemStack, savedEffects);
        HotpotSpriteProcessorDataComponent.addProcessor(itemStack, spriteProcessor);

        return itemStack;
    }

    @Override
    public void animateTick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, RandomSource randomSource) {

    }

    @Override
    public Optional<IHotpotContentFactory<?>> remapItemStack(boolean copy, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return Optional.of(HotpotContents.COOKING_RECIPE_CONTENT.get());
    }

    @Override
    public HotpotSoupTypeFactoryHolder<?> getSoupTypeFactoryHolder() {
        return soupTypeFactoryHolder;
    }

    @Override
    public float getWaterLevelDropRate() {
        return waterLevelDropRate;
    }

    @Override
    public boolean isHotpotLit(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return true;
    }

    public static class Factory extends AbstractHotpotFluidBasedSoupType.Factory<HotpotCookingRecipeSoupType> {
        private final float waterLevelDropRate;
        private final List<MobEffectInstance> savedEffects;
        private final Holder<IHotpotSpriteProcessor> spriteProcessor;

        public Factory(float waterLevelDropRate, List<MobEffectInstance> savedEffects, Holder<IHotpotSpriteProcessor> spriteProcessor) {
            this.waterLevelDropRate = waterLevelDropRate;
            this.savedEffects = savedEffects;
            this.spriteProcessor = spriteProcessor;
        }

        @Override
        public HotpotCookingRecipeSoupType buildFrom(HotpotSoupTypeFactoryHolder<HotpotCookingRecipeSoupType> soupTypeFactoryHolder, float waterLevel, float overflowWaterLevel, float activeness) {
            return new HotpotCookingRecipeSoupType(soupTypeFactoryHolder, waterLevelDropRate, savedEffects, spriteProcessor, waterLevel, overflowWaterLevel, activeness);
        }

        @Override
        public HotpotCookingRecipeSoupType buildFromScratch(HotpotSoupTypeFactoryHolder<HotpotCookingRecipeSoupType> soupTypeFactoryHolder) {
            return new HotpotCookingRecipeSoupType(soupTypeFactoryHolder, waterLevelDropRate, savedEffects, spriteProcessor);
        }

        @Override
        public IHotpotSoupFactorySerializer<HotpotCookingRecipeSoupType> getSerializer() {
            return HotpotSoupTypes.COOKING_RECIPE_SOUP_SERIALIZER.get();
        }

        public float getWaterLevelDropRate() {
            return waterLevelDropRate;
        }

        public List<MobEffectInstance> getSavedEffects() {
            return savedEffects;
        }

        public Holder<IHotpotSpriteProcessor> getSpriteProcessor() {
            return spriteProcessor;
        }
    }

    public static class Serializer implements IHotpotSoupFactorySerializer<HotpotCookingRecipeSoupType> {
        public static final StreamCodec<RegistryFriendlyByteBuf, Factory> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
                StreamCodec.composite(
                        ByteBufCodecs.FLOAT, HotpotCookingRecipeSoupType.Factory::getWaterLevelDropRate,
                        ByteBufCodecs.collection(ArrayList::new, MobEffectInstance.STREAM_CODEC), HotpotCookingRecipeSoupType.Factory::getSavedEffects,
                        HotpotSpriteProcessors.STREAM_CODEC, HotpotCookingRecipeSoupType.Factory::getSpriteProcessor,
                        HotpotCookingRecipeSoupType.Factory::new
                )
        );

        public static final MapCodec<Factory> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(factory -> factory.group(
                        Codec.FLOAT.fieldOf("water_level_drop_rate").forGetter(HotpotCookingRecipeSoupType.Factory::getWaterLevelDropRate),
                        MobEffectInstance.CODEC.listOf().optionalFieldOf("saved_effects", List.of()).forGetter(HotpotCookingRecipeSoupType.Factory::getSavedEffects),
                        HotpotSpriteProcessors.CODEC.optionalFieldOf("sprite_processor", HotpotSpriteProcessors.EMPTY_SPRITE_PROCESSOR).forGetter(HotpotCookingRecipeSoupType.Factory::getSpriteProcessor)
                ).apply(factory, HotpotCookingRecipeSoupType.Factory::new))
        );

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupTypeFactory<HotpotCookingRecipeSoupType>> getStreamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<? extends IHotpotSoupTypeFactory<HotpotCookingRecipeSoupType>> getCodec() {
            return CODEC;
        }
    }
}