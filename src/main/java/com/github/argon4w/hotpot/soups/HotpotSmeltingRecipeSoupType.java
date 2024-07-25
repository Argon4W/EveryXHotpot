package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotContents;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class HotpotSmeltingRecipeSoupType extends AbstractHotpotFluidBasedSoupType {
    private final ResourceLocation resourceLocation;
    private final float waterLevelDropRate;

    public HotpotSmeltingRecipeSoupType(ResourceLocation resourceLocation, float waterLevelDropRate) {
        this.resourceLocation = resourceLocation;
        this.waterLevelDropRate = waterLevelDropRate;

        this.waterLevel = 0.0f;
        this.overflowWaterLevel = 0.0f;
        this.activeness = 0.0f;
    }

    public HotpotSmeltingRecipeSoupType(ResourceLocation resourceLocation, float waterLevelDropRate, float waterLevel, float overflowWaterLevel, float activeness) {
        this.resourceLocation = resourceLocation;
        this.waterLevelDropRate = waterLevelDropRate;

        this.waterLevel = waterLevel;
        this.overflowWaterLevel = overflowWaterLevel;
        this.activeness = activeness;
    }

    @Override
    public Optional<IHotpotContent> remapItemStack(boolean copy, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return Optional.of(HotpotContents.SMELTING_RECIPE_CONTENT.get().buildFromItem(itemStack, hotpotBlockEntity));
    }

    @Override
    public void animateTick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, RandomSource randomSource) {

    }

    @Override
    public float getWaterLevelDropRate() {
        return waterLevelDropRate;
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }

    @Override
    public boolean isHotpotLit(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return true;
    }

    public static class Factory extends AbstractHotpotFluidBasedSoupType.Factory<HotpotSmeltingRecipeSoupType> {
        private final float waterLevelDropRate;

        public Factory(float waterLevelDropRate) {
            this.waterLevelDropRate = waterLevelDropRate;
        }

        @Override
        public HotpotSmeltingRecipeSoupType buildFrom(ResourceLocation resourceLocation, float waterLevel, float overflowWaterLevel, float activeness) {
            return new HotpotSmeltingRecipeSoupType(resourceLocation, waterLevelDropRate, waterLevel, overflowWaterLevel, activeness);
        }

        @Override
        public HotpotSmeltingRecipeSoupType buildFromScratch(ResourceLocation resourceLocation) {
            return new HotpotSmeltingRecipeSoupType(resourceLocation, waterLevelDropRate);
        }

        @Override
        public IHotpotSoupFactorySerializer<HotpotSmeltingRecipeSoupType> getSerializer() {
            return HotpotSoupTypes.SMELTING_RECIPE_SOUP_SERIALIZER.get();
        }

        public float getWaterLevelDropRate() {
            return waterLevelDropRate;
        }
    }

    public static class Serializer implements IHotpotSoupFactorySerializer<HotpotSmeltingRecipeSoupType> {
        public static final StreamCodec<RegistryFriendlyByteBuf, Factory> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, Factory::getWaterLevelDropRate,
                Factory::new
        );

        public static final MapCodec<Factory> CODEC = RecordCodecBuilder.mapCodec(factory -> factory.group(
                Codec.FLOAT.fieldOf("water_level_drop_rate").forGetter(Factory::getWaterLevelDropRate)
        ).apply(factory, Factory::new));

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupFactory<HotpotSmeltingRecipeSoupType>> getStreamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<? extends IHotpotSoupFactory<HotpotSmeltingRecipeSoupType>> getCodec() {
            return CODEC;
        }
    }
}
