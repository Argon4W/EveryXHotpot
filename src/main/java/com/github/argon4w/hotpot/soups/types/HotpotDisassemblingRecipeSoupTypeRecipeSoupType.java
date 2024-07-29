package com.github.argon4w.hotpot.soups.types;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotContents;
import com.github.argon4w.hotpot.contents.IHotpotContentFactory;
import com.github.argon4w.hotpot.soups.HotpotSoupTypeFactoryHolder;
import com.github.argon4w.hotpot.soups.HotpotSoupTypes;
import com.github.argon4w.hotpot.soups.IHotpotSoupFactorySerializer;
import com.github.argon4w.hotpot.soups.IHotpotSoupTypeFactory;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class HotpotDisassemblingRecipeSoupTypeRecipeSoupType extends AbstractHotpotFluidBasedSoupType {
    private final HotpotSoupTypeFactoryHolder<?> soupTypeFactoryHolder;
    private final float waterLevelDropRate;

    public HotpotDisassemblingRecipeSoupTypeRecipeSoupType(HotpotSoupTypeFactoryHolder<?> soupTypeFactoryHolder, float waterLevelDropRate) {
        this.soupTypeFactoryHolder = soupTypeFactoryHolder;
        this.waterLevelDropRate = waterLevelDropRate;

        this.waterLevel = 0.0f;
        this.overflowWaterLevel = 0.0f;
        this.activeness = 0.0f;
    }


    public HotpotDisassemblingRecipeSoupTypeRecipeSoupType(HotpotSoupTypeFactoryHolder<?> soupTypeFactoryHolder, float waterLevelDropRate, float waterLevel, float overflowWaterLevel, float activeness) {
        this.soupTypeFactoryHolder = soupTypeFactoryHolder;
        this.waterLevelDropRate = waterLevelDropRate;

        this.waterLevel = waterLevel;
        this.overflowWaterLevel = overflowWaterLevel;
        this.activeness = activeness;
    }

    @Override
    public void animateTick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, RandomSource randomSource) {

    }

    @Override
    public Optional<IHotpotContentFactory<?>> remapItemStack(boolean copy, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return Optional.of(HotpotContents.DISASSEMBLING_RECIPE_CONTENT.get());
    }

    @Override
    public float getWaterLevelDropRate() {
        return waterLevelDropRate;
    }

    @Override
    public boolean isHotpotLit(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return false;
    }

    @Override
    public boolean canItemEnter(ItemEntity itemEntity) {
        return !itemEntity.hasPickUpDelay();
    }

    @Override
    public HotpotSoupTypeFactoryHolder<?> getSoupTypeFactoryHolder() {
        return soupTypeFactoryHolder;
    }

    public static class Factory extends AbstractHotpotFluidBasedSoupType.Factory<HotpotDisassemblingRecipeSoupTypeRecipeSoupType> {
        private final float waterLevelDropRate;

        public Factory(float waterLevelDropRate) {
            this.waterLevelDropRate = waterLevelDropRate;
        }

        @Override
        public HotpotDisassemblingRecipeSoupTypeRecipeSoupType buildFrom(HotpotSoupTypeFactoryHolder<HotpotDisassemblingRecipeSoupTypeRecipeSoupType> soupTypeFactoryHolder, float waterLevel, float overflowWaterLevel, float activeness) {
            return new HotpotDisassemblingRecipeSoupTypeRecipeSoupType(soupTypeFactoryHolder, waterLevelDropRate, waterLevel, overflowWaterLevel, activeness);
        }

        @Override
        public HotpotDisassemblingRecipeSoupTypeRecipeSoupType buildFromScratch(HotpotSoupTypeFactoryHolder<HotpotDisassemblingRecipeSoupTypeRecipeSoupType> soupTypeFactoryHolder) {
            return new HotpotDisassemblingRecipeSoupTypeRecipeSoupType(soupTypeFactoryHolder, waterLevelDropRate);
        }

        @Override
        public IHotpotSoupFactorySerializer<HotpotDisassemblingRecipeSoupTypeRecipeSoupType> getSerializer() {
            return HotpotSoupTypes.DISASSEMBLING_RECIPE_SOUP_SERIALIZER.get();
        }

        public float waterLevelDropRate() {
            return waterLevelDropRate;
        }
    }

    public static class Serializer implements IHotpotSoupFactorySerializer<HotpotDisassemblingRecipeSoupTypeRecipeSoupType> {
        public static final StreamCodec<RegistryFriendlyByteBuf, Factory> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, HotpotDisassemblingRecipeSoupTypeRecipeSoupType.Factory::waterLevelDropRate,
                HotpotDisassemblingRecipeSoupTypeRecipeSoupType.Factory::new
        );

        public static final MapCodec<Factory> CODEC = RecordCodecBuilder.mapCodec(factory -> factory.group(
                Codec.FLOAT.fieldOf("water_level_drop_rate").forGetter(HotpotDisassemblingRecipeSoupTypeRecipeSoupType.Factory::waterLevelDropRate)
        ).apply(factory, HotpotDisassemblingRecipeSoupTypeRecipeSoupType.Factory::new));

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupTypeFactory<HotpotDisassemblingRecipeSoupTypeRecipeSoupType>> getStreamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<? extends IHotpotSoupTypeFactory<HotpotDisassemblingRecipeSoupTypeRecipeSoupType>> getCodec() {
            return CODEC;
        }
    }
}