package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.HotpotTagsHelper;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.client.items.process.HotpotSpriteProcessors;
import com.github.argon4w.hotpot.contents.HotpotContents;
import com.github.argon4w.hotpot.contents.HotpotCookingRecipeContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.items.HotpotSkewerItem;
import com.github.argon4w.hotpot.soups.effects.HotpotEffectHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class HotpotCookingRecipeSoupType extends AbstractHotpotFluidBasedSoupType {
    private final ResourceLocation resourceLocation;
    private final float waterLevelDropRate;
    private final List<MobEffectInstance> savedEffects;
    private final ResourceLocation processorResourceLocation;

    public HotpotCookingRecipeSoupType(ResourceLocation resourceLocation, float waterLevelDropRate, List<MobEffectInstance> savedEffects, ResourceLocation processorResourceLocation) {
        this.resourceLocation = resourceLocation;
        this.waterLevelDropRate = waterLevelDropRate;
        this.savedEffects = savedEffects;
        this.processorResourceLocation = processorResourceLocation;

        this.waterLevel = 0.0f;
        this.overflowWaterLevel = 0.0f;
        this.activeness = 0.0f;
    }

    public HotpotCookingRecipeSoupType(ResourceLocation resourceLocation, float waterLevelDropRate, List<MobEffectInstance> savedEffects, ResourceLocation processorResourceLocation, float waterLevel, float overflowWaterLevel, float activeness) {
        this.resourceLocation = resourceLocation;
        this.waterLevelDropRate = waterLevelDropRate;
        this.savedEffects = savedEffects;
        this.processorResourceLocation = processorResourceLocation;

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

        if (HotpotTagsHelper.getHotpotTags(itemStack).contains("Soup", Tag.TAG_STRING)) {
            return itemStack;
        }

        HotpotTagsHelper.updateHotpotTags(itemStack, "Soup", StringTag.valueOf(getResourceLocation().toString()));
        HotpotEffectHelper.saveEffects(itemStack, savedEffects);

        if (processorResourceLocation == HotpotSpriteProcessors.EMPTY_SPRITE_PROCESSOR_LOCATION) {
            return itemStack;
        }

        HotpotSpriteProcessors.applyProcessor(processorResourceLocation, itemStack);

        return itemStack;
    }

    @Override
    public void animateTick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, RandomSource randomSource) {

    }

    @Override
    public Optional<IHotpotContent> remapItemStack(boolean copy, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return Optional.of(HotpotContents.COOKING_RECIPE_CONTENT.get().buildFromItem(itemStack, hotpotBlockEntity));
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return resourceLocation;
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
        private final ResourceLocation processorResourceLocation;

        public Factory(float waterLevelDropRate, List<MobEffectInstance> savedEffects, ResourceLocation processorResourceLocation) {
            this.waterLevelDropRate = waterLevelDropRate;
            this.savedEffects = savedEffects;
            this.processorResourceLocation = processorResourceLocation;
        }

        @Override
        public HotpotCookingRecipeSoupType buildFrom(ResourceLocation resourceLocation, float waterLevel, float overflowWaterLevel, float activeness) {
            return new HotpotCookingRecipeSoupType(resourceLocation, waterLevelDropRate, savedEffects, processorResourceLocation, waterLevel, overflowWaterLevel, activeness);
        }

        @Override
        public HotpotCookingRecipeSoupType buildFromScratch(ResourceLocation resourceLocation) {
            return new HotpotCookingRecipeSoupType(resourceLocation, waterLevelDropRate, savedEffects, processorResourceLocation);
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

        public ResourceLocation getProcessorResourceLocation() {
            return processorResourceLocation;
        }
    }

    public static class Serializer implements IHotpotSoupFactorySerializer<HotpotCookingRecipeSoupType> {
        public static final StreamCodec<RegistryFriendlyByteBuf, Factory> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, Factory::getWaterLevelDropRate,
                ByteBufCodecs.collection(ArrayList::new, MobEffectInstance.STREAM_CODEC), Factory::getSavedEffects,
                ResourceLocation.STREAM_CODEC, Factory::getProcessorResourceLocation,
                Factory::new
        );

        public static final MapCodec<Factory> CODEC = RecordCodecBuilder.mapCodec(factory -> factory.group(
                Codec.FLOAT.fieldOf("water_level_drop_rate").forGetter(Factory::getWaterLevelDropRate),
                MobEffectInstance.CODEC.listOf().optionalFieldOf("saved_effects", List.of()).forGetter(Factory::getSavedEffects),
                ResourceLocation.CODEC.optionalFieldOf("sauced_processor", HotpotSpriteProcessors.EMPTY_SPRITE_PROCESSOR_LOCATION).forGetter(Factory::getProcessorResourceLocation)
        ).apply(factory, Factory::new));

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupFactory<HotpotCookingRecipeSoupType>> getStreamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<? extends IHotpotSoupFactory<HotpotCookingRecipeSoupType>> getCodec() {
            return CODEC;
        }
    }
}