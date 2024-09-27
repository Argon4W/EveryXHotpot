package com.github.argon4w.hotpot.soups.recipes;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.codecs.LazyMapCodec;
import com.github.argon4w.hotpot.soups.HotpotComponentSoupType;
import com.github.argon4w.hotpot.soups.recipes.effects.HotpotRandomMobEffectMap;
import com.github.argon4w.hotpot.soups.recipes.effects.IHotpotRandomMobEffectKey;
import com.github.argon4w.hotpot.soups.recipes.input.HotpotRecipeInput;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.Optional;

public class HotpotSoupRandomMobEffectRecipe extends AbstractHotpotCommonInputRecipe {
    private final ResourceKey<HotpotComponentSoupType> targetSoupTypeKey;
    private final Ingredient ingredient;
    private final Holder<HotpotRandomMobEffectMap> mobEffectMapHolder;
    private final IHotpotRandomMobEffectKey.Wrapper mobEffectKey;
    private final ItemStack remainingItem;
    private final Holder<SoundEvent> soundEvent;

    public HotpotSoupRandomMobEffectRecipe(ResourceKey<HotpotComponentSoupType> targetSoupTypeKey, Ingredient ingredient, Holder<HotpotRandomMobEffectMap> mobEffectMap, IHotpotRandomMobEffectKey.Wrapper mobEffectKey, ItemStack remainingItem, Holder<SoundEvent> soundEvent) {
        this.targetSoupTypeKey = targetSoupTypeKey;
        this.ingredient = ingredient;
        this.mobEffectMapHolder = mobEffectMap;
        this.mobEffectKey = mobEffectKey;
        this.remainingItem = remainingItem;
        this.soundEvent = soundEvent;
    }

    @Override
    public boolean matches(HotpotRecipeInput input, Level level) {
        return targetSoupTypeKey.equals(input.soup().soupTypeHolder().getKey()) && ingredient.test(input.itemStack());
    }

    public Optional<MobEffectInstance> getMobEffect() {
        return mobEffectMapHolder.value().getEffect(mobEffectKey);
    }

    public ResourceKey<HotpotComponentSoupType> getTargetSoupTypeKey() {
        return targetSoupTypeKey;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public ItemStack getRemainingItem() {
        return remainingItem.copy();
    }

    public Holder<SoundEvent> getSoundEvent() {
        return soundEvent;
    }

    public Holder<HotpotRandomMobEffectMap> getMobEffectMapHolder() {
        return mobEffectMapHolder;
    }

    public IHotpotRandomMobEffectKey.Wrapper getMobEffectKey() {
        return mobEffectKey;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return HotpotModEntry.HOTPOT_SOUP_RANDOM_MOB_EFFECT_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return HotpotModEntry.HOTPOT_SOUP_RANDOM_MOB_EFFECT_RECIPE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<HotpotSoupRandomMobEffectRecipe> {
        public static final MapCodec<HotpotSoupRandomMobEffectRecipe> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(recipe -> recipe.group(
                        HotpotComponentSoupType.KEY_CODEC.fieldOf("target_soup").forGetter(HotpotSoupRandomMobEffectRecipe::getTargetSoupTypeKey),
                        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(HotpotSoupRandomMobEffectRecipe::getIngredient),
                        HotpotRandomMobEffectMap.HOLDER_CODEC.fieldOf("mob_effect_map").forGetter(HotpotSoupRandomMobEffectRecipe::getMobEffectMapHolder),
                        IHotpotRandomMobEffectKey.CODEC.fieldOf("mob_effect_key").forGetter(HotpotSoupRandomMobEffectRecipe::getMobEffectKey),
                        ItemStack.OPTIONAL_CODEC.optionalFieldOf("remaining_item", ItemStack.EMPTY).forGetter(HotpotSoupRandomMobEffectRecipe::getRemainingItem),
                        SoundEvent.CODEC.fieldOf("sound_event").forGetter(HotpotSoupRandomMobEffectRecipe::getSoundEvent)
                ).apply(recipe, HotpotSoupRandomMobEffectRecipe::new))
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, HotpotSoupRandomMobEffectRecipe> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
                StreamCodec.composite(
                        HotpotComponentSoupType.KEY_STREAM_CODEC, HotpotSoupRandomMobEffectRecipe::getTargetSoupTypeKey,
                        Ingredient.CONTENTS_STREAM_CODEC, HotpotSoupRandomMobEffectRecipe::getIngredient,
                        HotpotRandomMobEffectMap.HOLDER_STREAM_CODEC, HotpotSoupRandomMobEffectRecipe::getMobEffectMapHolder,
                        IHotpotRandomMobEffectKey.STREAM_CODEC, HotpotSoupRandomMobEffectRecipe::getMobEffectKey,
                        ItemStack.OPTIONAL_STREAM_CODEC, HotpotSoupRandomMobEffectRecipe::getRemainingItem,
                        SoundEvent.STREAM_CODEC, HotpotSoupRandomMobEffectRecipe::getSoundEvent,
                        HotpotSoupRandomMobEffectRecipe::new
                )
        );

        @Override
        public MapCodec<HotpotSoupRandomMobEffectRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotSoupRandomMobEffectRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
