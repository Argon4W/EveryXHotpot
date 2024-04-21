package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.HotpotTagsHelper;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.client.items.process.HotpotSpriteProcessors;
import com.github.argon4w.hotpot.contents.HotpotCookingRecipeContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.items.HotpotSkewerItem;
import com.github.argon4w.hotpot.items.IHotpotItemContainer;
import com.github.argon4w.hotpot.soups.effects.HotpotEffectHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.apache.commons.compress.utils.Lists;

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

        if (result.getItem() instanceof HotpotSkewerItem) {
            return HotpotSkewerItem.applyToSkewerItemStacks(result, this::apply);
        }

        return apply(result);
    }

    public ItemStack apply(ItemStack itemStack) {
        if (!itemStack.isEdible()) {
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
    public Optional<IHotpotContent> remapItemStack(boolean copy, ItemStack itemStack, LevelBlockPos pos) {
        return Optional.of(new HotpotCookingRecipeContent((copy ? itemStack.copy() : itemStack)));
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }

    @Override
    public float getWaterLevelDropRate() {
        return waterLevelDropRate;
    }

    public record Factory(ResourceLocation resourceLocation, float waterLevelDropRate, List<MobEffectInstance> savedEffects, ResourceLocation processorResourceLocation) implements IHotpotSoupFactory<HotpotCookingRecipeSoupType> {
        @Override
        public HotpotCookingRecipeSoupType build() {
            return new HotpotCookingRecipeSoupType(resourceLocation, waterLevelDropRate, savedEffects, processorResourceLocation);
        }

        @Override
        public IHotpotSoupTypeSerializer<HotpotCookingRecipeSoupType> getSerializer() {
            return HotpotSoupTypes.COOKING_RECIPE_SOUP_SERIALIZER.get();
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return resourceLocation;
        }
    }

    public static class Serializer implements IHotpotSoupTypeSerializer<HotpotCookingRecipeSoupType> {
        @Override
        public Factory fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            if (!jsonObject.has("water_level_drop_rate")) {
                throw new JsonParseException("Cooking recipe soup must have a \"water_level_drop_rate\"");
            }

            float waterLevelDropRate = GsonHelper.getAsFloat(jsonObject, "water_level_drop_rate");
            ArrayList<MobEffectInstance> savedEffects = Lists.newArrayList();

            for (JsonElement jsonElement : GsonHelper.getAsJsonArray(jsonObject, "saved_effects", new JsonArray())) {
                if (!jsonElement.isJsonObject()) {
                    throw new JsonParseException("Mob effect must be a JSON object");
                }

                JsonObject mobEffectJsonObject = jsonElement.getAsJsonObject();
                savedEffects.add(MobEffectInstance.load(CraftingHelper.getNBT(mobEffectJsonObject)));
            }

            if (!jsonObject.has("sauced_processor")) {
                return new Factory(resourceLocation, waterLevelDropRate, savedEffects, HotpotSpriteProcessors.EMPTY_SPRITE_PROCESSOR_LOCATION);
            }

            if (!ResourceLocation.isValidResourceLocation(GsonHelper.getAsString(jsonObject, "sauced_processor"))) {
                throw new JsonParseException("\"sauced_processor\" in the cooking recipe soup must be a valid resource location");
            }

            ResourceLocation processorResourceLocation = new ResourceLocation(GsonHelper.getAsString(jsonObject, "sauced_processor"));

            return new Factory(resourceLocation, waterLevelDropRate, savedEffects, processorResourceLocation);
        }

        @Override
        public Factory fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf byteBuf) {
            float waterLevelDropRate = byteBuf.readFloat();
            List<CompoundTag> tagList = byteBuf.readList(FriendlyByteBuf::readNbt);
            List<MobEffectInstance> savedEffects = tagList.stream().map(MobEffectInstance::load).toList();
            ResourceLocation processorResourceLocation = byteBuf.readResourceLocation();

            return new Factory(resourceLocation, waterLevelDropRate, savedEffects, processorResourceLocation);
        }

        @Override
        public void toNetwork(IHotpotSoupFactory<HotpotCookingRecipeSoupType> factory, FriendlyByteBuf byteBuf) {
            HotpotCookingRecipeSoupType cookingRecipeSoupType = factory.build();

            byteBuf.writeFloat(cookingRecipeSoupType.waterLevelDropRate);
            byteBuf.writeCollection(cookingRecipeSoupType.savedEffects, this::writeSingleEffect);
            byteBuf.writeResourceLocation(cookingRecipeSoupType.processorResourceLocation);
        }

        private void writeSingleEffect(FriendlyByteBuf byteBuf, MobEffectInstance effect) {
            byteBuf.writeNbt(effect.save(new CompoundTag()));
        }
    }
}