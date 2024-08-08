package com.github.argon4w.hotpot.soups.types;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotContentSerializers;
import com.github.argon4w.hotpot.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.soups.HotpotSoupTypeHolder;
import com.github.argon4w.hotpot.soups.HotpotSoupTypeSerializers;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import com.github.argon4w.hotpot.soups.IHotpotSoupTypeSerializer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class HotpotDisassemblingRecipeSoupTypeRecipeSoup extends AbstractHotpotFluidBasedSoup {
    private final HotpotSoupTypeHolder<?> soupTypeHolder;
    private final float waterLevelDropRate;

    public HotpotDisassemblingRecipeSoupTypeRecipeSoup(HotpotSoupTypeHolder<?> soupTypeHolder, float waterLevelDropRate) {
        this.soupTypeHolder = soupTypeHolder;
        this.waterLevelDropRate = waterLevelDropRate;

        this.waterLevel = 0.0f;
        this.overflowWaterLevel = 0.0f;
        this.activeness = 0.0f;
    }

    public HotpotDisassemblingRecipeSoupTypeRecipeSoup(HotpotSoupTypeHolder<?> soupTypeHolder, float waterLevelDropRate, float waterLevel, float overflowWaterLevel, float activeness) {
        this.soupTypeHolder = soupTypeHolder;
        this.waterLevelDropRate = waterLevelDropRate;

        this.waterLevel = waterLevel;
        this.overflowWaterLevel = overflowWaterLevel;
        this.activeness = activeness;
    }

    @Override
    public void animateTick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, RandomSource randomSource) {

    }

    @Override
    public Optional<IHotpotContentSerializer<?>> getContentSerializerFromItemStack(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return itemStack.isEmpty() ? Optional.empty() : Optional.of(HotpotContentSerializers.DISASSEMBLING_RECIPE_CONTENT_SERIALIZER.get());
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
    public HotpotSoupTypeHolder<?> getSoupTypeHolder() {
        return soupTypeHolder;
    }

    public static class Type extends AbstractHotpotFluidBasedSoup.Type<HotpotDisassemblingRecipeSoupTypeRecipeSoup> {
        private final float waterLevelDropRate;

        public Type(float waterLevelDropRate) {
            this.waterLevelDropRate = waterLevelDropRate;
        }

        @Override
        public HotpotDisassemblingRecipeSoupTypeRecipeSoup buildFrom(HotpotSoupTypeHolder<HotpotDisassemblingRecipeSoupTypeRecipeSoup> soupTypeHolder, float waterLevel, float overflowWaterLevel, float activeness) {
            return new HotpotDisassemblingRecipeSoupTypeRecipeSoup(soupTypeHolder, waterLevelDropRate, waterLevel, overflowWaterLevel, activeness);
        }

        @Override
        public HotpotDisassemblingRecipeSoupTypeRecipeSoup getSoup(HotpotSoupTypeHolder<HotpotDisassemblingRecipeSoupTypeRecipeSoup> soupTypeHolder) {
            return new HotpotDisassemblingRecipeSoupTypeRecipeSoup(soupTypeHolder, waterLevelDropRate);
        }

        @Override
        public Holder<IHotpotSoupTypeSerializer<?>> getSerializer() {
            return HotpotSoupTypeSerializers.DISASSEMBLING_RECIPE_SOUP_TYPE_SERIALIZER;
        }

        public float waterLevelDropRate() {
            return waterLevelDropRate;
        }
    }

    public static class Serializer implements IHotpotSoupTypeSerializer<HotpotDisassemblingRecipeSoupTypeRecipeSoup> {
        public static final StreamCodec<RegistryFriendlyByteBuf, Type> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, Type::waterLevelDropRate,
                Type::new
        );

        public static final MapCodec<Type> CODEC = RecordCodecBuilder.mapCodec(serializer -> serializer.group(
                Codec.FLOAT.fieldOf("water_level_drop_rate").forGetter(Type::waterLevelDropRate)
        ).apply(serializer, Type::new));

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupType<HotpotDisassemblingRecipeSoupTypeRecipeSoup>> getStreamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<? extends IHotpotSoupType<HotpotDisassemblingRecipeSoupTypeRecipeSoup>> getCodec() {
            return CODEC;
        }
    }
}