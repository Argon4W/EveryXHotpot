package com.github.argon4w.hotpot.soups.types;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LazyMapCodec;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotContentSerializers;
import com.github.argon4w.hotpot.contents.HotpotCookingRecipeContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.items.HotpotSkewerItem;
import com.github.argon4w.hotpot.items.components.HotpotFoodEffectsDataComponent;
import com.github.argon4w.hotpot.items.components.HotpotSoupDataComponent;
import com.github.argon4w.hotpot.items.components.HotpotSpriteConfigDataComponent;
import com.github.argon4w.hotpot.items.sprites.HotpotSpriteConfigSerializers;
import com.github.argon4w.hotpot.items.sprites.IHotpotSpriteConfig;
import com.github.argon4w.hotpot.soups.HotpotSoupTypeHolder;
import com.github.argon4w.hotpot.soups.HotpotSoupTypeSerializers;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import com.github.argon4w.hotpot.soups.IHotpotSoupTypeSerializer;
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
import org.joml.Math;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HotpotCookingRecipeSoup extends AbstractHotpotFluidBasedSoup {
    private final HotpotSoupTypeHolder<?> soupTypeHolder;
    private final float waterLevelDropRate;
    private final List<MobEffectInstance> savedEffects;
    private final List<IHotpotSpriteConfig> spriteConfigs;

    public int emptyWaterPunishCooldown;

    public HotpotCookingRecipeSoup(HotpotSoupTypeHolder<?> soupTypeHolder, float waterLevelDropRate, List<MobEffectInstance> savedEffects, List<IHotpotSpriteConfig> spriteConfigs) {
        this.soupTypeHolder = soupTypeHolder;
        this.waterLevelDropRate = waterLevelDropRate;
        this.savedEffects = savedEffects;
        this.spriteConfigs = spriteConfigs;

        this.waterLevel = 0.0f;
        this.overflowWaterLevel = 0.0f;
        this.activeness = 0.0f;
        this.emptyWaterPunishCooldown = 0;
    }

    public HotpotCookingRecipeSoup(HotpotSoupTypeHolder<?> soupTypeHolder, float waterLevelDropRate, List<MobEffectInstance> savedEffects, List<IHotpotSpriteConfig> spriteConfigs, float waterLevel, float overflowWaterLevel, float activeness, int emptyWaterPunishCooldown) {
        this.soupTypeHolder = soupTypeHolder;
        this.waterLevelDropRate = waterLevelDropRate;
        this.savedEffects = savedEffects;
        this.spriteConfigs = spriteConfigs;

        this.waterLevel = waterLevel;
        this.overflowWaterLevel = overflowWaterLevel;
        this.activeness = activeness;
        this.emptyWaterPunishCooldown = emptyWaterPunishCooldown;
    }

    @Override
    public void tick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        super.tick(hotpotBlockEntity, pos);
        emptyWaterPunishCooldown = waterLevel > 0.0f ? Math.max(0, emptyWaterPunishCooldown - 1) : Math.min(20 * 60, Math.max(20 * 30, emptyWaterPunishCooldown) + 1);
    }

    @Override
    public void onContentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        super.onContentUpdate(content, hotpotBlockEntity, pos);

        if (emptyWaterPunishCooldown > 0) {
            return;
        }

        if (waterLevel <= 0.0f) {
            return;
        }

        if (!(content instanceof HotpotCookingRecipeContent cookingRecipeContent)) {
            return;
        }

        ItemStack itemStack = cookingRecipeContent.getItemStack();

        if (cookingRecipeContent.getCookingTime() >= 0) {
            return;
        }

        if (itemStack.is(HotpotModEntry.HOTPOT_SKEWER)) {
            HotpotSkewerItem.applyToSkewerItemStacks(itemStack, this::applySpriteConfigsAndEffects);
            return;
        }

        if (!itemStack.has(DataComponents.FOOD)) {
            return;
        }

        applySpriteConfigsAndEffects(itemStack);
    }

    public ItemStack applySpriteConfigsAndEffects(ItemStack itemStack) {
        if (!itemStack.has(DataComponents.FOOD)) {
            return itemStack;
        }

        if (HotpotSoupDataComponent.hasDataComponent(itemStack)) {
            return itemStack;
        }

        if (HotpotFoodEffectsDataComponent.hasDataComponent(itemStack)) {
            return itemStack;
        }

        HotpotSoupDataComponent.setSoup(itemStack, this);
        HotpotFoodEffectsDataComponent.addEffects(itemStack, savedEffects);
        HotpotSpriteConfigDataComponent.addSpriteConfigs(itemStack, spriteConfigs);

        return itemStack;
    }

    @Override
    public Optional<IHotpotContentSerializer<?>> getContentSerializerFromItemStack(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return itemStack.isEmpty() ? Optional.empty() : Optional.of(HotpotContentSerializers.COOKING_RECIPE_CONTENT_SERIALIZER.get());
    }

    @Override
    public HotpotSoupTypeHolder<?> getSoupTypeHolder() {
        return soupTypeHolder;
    }

    @Override
    public float getWaterLevelDropRate() {
        return waterLevelDropRate;
    }

    @Override
    public boolean isHotpotLit(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return true;
    }

    @Override
    public void animateTick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, RandomSource randomSource) {

    }

    public int getEmptyWaterPunishCooldown() {
        return emptyWaterPunishCooldown;
    }

    public static class Type implements IHotpotSoupType<HotpotCookingRecipeSoup> {
        private final float waterLevelDropRate;
        private final List<MobEffectInstance> savedEffects;
        private final List<IHotpotSpriteConfig> spriteConfigs;

        public Type(float waterLevelDropRate, List<MobEffectInstance> savedEffects, List<IHotpotSpriteConfig> spriteConfigs) {
            this.waterLevelDropRate = waterLevelDropRate;
            this.savedEffects = savedEffects;
            this.spriteConfigs = spriteConfigs;
        }

        @Override
        public MapCodec<HotpotCookingRecipeSoup> getCodec(HotpotSoupTypeHolder<HotpotCookingRecipeSoup> soupTypeHolder) {
            return RecordCodecBuilder.mapCodec(soupType -> soupType.group(
                    Codec.FLOAT.fieldOf("waterlevel").forGetter(HotpotCookingRecipeSoup::getWaterLevel),
                    Codec.FLOAT.fieldOf("overflow_waterlevel").forGetter(HotpotCookingRecipeSoup::getOverflowWaterLevel),
                    Codec.FLOAT.fieldOf("activeness").forGetter(HotpotCookingRecipeSoup::getActiveness),
                    Codec.INT.fieldOf("empty_water_punish_cooldown").forGetter(HotpotCookingRecipeSoup::getEmptyWaterPunishCooldown)
            ).apply(soupType, (waterLevel, overflowWaterLevel, activeness, emptyWaterPunishCooldown) -> buildFrom(soupTypeHolder, waterLevel, overflowWaterLevel, activeness, emptyWaterPunishCooldown)));
        }

        public HotpotCookingRecipeSoup buildFrom(HotpotSoupTypeHolder<HotpotCookingRecipeSoup> soupTypeHolder, float waterLevel, float overflowWaterLevel, float activeness, int emptyWaterPunishCooldown) {
            return new HotpotCookingRecipeSoup(soupTypeHolder, waterLevelDropRate, savedEffects, spriteConfigs, waterLevel, overflowWaterLevel, activeness, emptyWaterPunishCooldown);
        }

        @Override
        public HotpotCookingRecipeSoup getSoup(HotpotSoupTypeHolder<HotpotCookingRecipeSoup> soupTypeHolder) {
            return new HotpotCookingRecipeSoup(soupTypeHolder, waterLevelDropRate, savedEffects, spriteConfigs);
        }

        @Override
        public Holder<IHotpotSoupTypeSerializer<?>> getSerializer() {
            return HotpotSoupTypeSerializers.COOKING_RECIPE_SOUP_TYPE_SERIALIZER;
        }

        public float getWaterLevelDropRate() {
            return waterLevelDropRate;
        }

        public List<MobEffectInstance> getSavedEffects() {
            return savedEffects;
        }

        public List<IHotpotSpriteConfig> getSpriteConfigs() {
            return spriteConfigs;
        }
    }

    public static class Serializer implements IHotpotSoupTypeSerializer<HotpotCookingRecipeSoup> {
        public static final StreamCodec<RegistryFriendlyByteBuf, Type> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
                StreamCodec.composite(
                        ByteBufCodecs.FLOAT, Type::getWaterLevelDropRate,
                        ByteBufCodecs.collection(ArrayList::new, MobEffectInstance.STREAM_CODEC), Type::getSavedEffects,
                        ByteBufCodecs.collection(ArrayList::new, HotpotSpriteConfigSerializers.STREAM_CODEC), Type::getSpriteConfigs,
                        Type::new
                )
        );

        public static final MapCodec<Type> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(serializer -> serializer.group(
                        Codec.FLOAT.fieldOf("water_level_drop_rate").forGetter(Type::getWaterLevelDropRate),
                        MobEffectInstance.CODEC.listOf().optionalFieldOf("saved_effects", List.of()).forGetter(Type::getSavedEffects),
                        HotpotSpriteConfigSerializers.CODEC.listOf().optionalFieldOf("sprite_configs", List.of()).forGetter(Type::getSpriteConfigs)
                ).apply(serializer, Type::new))
        );

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupType<HotpotCookingRecipeSoup>> getStreamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<? extends IHotpotSoupType<HotpotCookingRecipeSoup>> getCodec() {
            return CODEC;
        }
    }
}