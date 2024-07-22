package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotSmeltingRecipeContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class HotpotSmeltingRecipeSoupType extends AbstractHotpotFluidBasedSoupType {
    private final ResourceLocation resourceLocation;
    private final float waterLevelDropRate;

    public HotpotSmeltingRecipeSoupType(ResourceLocation resourceLocation, float waterLevelDropRate) {
        this.resourceLocation = resourceLocation;
        this.waterLevelDropRate = waterLevelDropRate;
    }

    @Override
    public Optional<IHotpotContent> remapItemStack(boolean copy, ItemStack itemStack, LevelBlockPos pos) {
        return HotpotSmeltingRecipeContent.hasSmeltingRecipe(itemStack, pos) ? Optional.of(new HotpotSmeltingRecipeContent((copy ? itemStack.copy() : itemStack))) : Optional.empty();
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

    public record Factory(ResourceLocation resourceLocation, float waterLevelDropRate) implements IHotpotSoupFactory<HotpotSmeltingRecipeSoupType> {
        @Override
        public HotpotSmeltingRecipeSoupType build() {
            return new HotpotSmeltingRecipeSoupType(resourceLocation, waterLevelDropRate);
        }

        @Override
        public IHotpotSoupTypeSerializer<HotpotSmeltingRecipeSoupType> getSerializer() {
            return HotpotSoupTypes.SMELTING_RECIPE_SOUP_SERIALIZER.get();
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return resourceLocation;
        }
    }

    public static class Serializer implements IHotpotSoupTypeSerializer<HotpotSmeltingRecipeSoupType> {
        @Override
        public Factory fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            if (!jsonObject.has("water_level_drop_rate")) {
                throw new JsonParseException("Cooking recipe soup must have a \"water_level_drop_rate\"");
            }

            float waterLevelDropRate = GsonHelper.getAsFloat(jsonObject, "water_level_drop_rate");

            return new Factory(resourceLocation, waterLevelDropRate);
        }

        @Override
        public Factory fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf byteBuf) {
            float waterLevelDropRate = byteBuf.readFloat();
            return new Factory(resourceLocation, waterLevelDropRate);
        }

        @Override
        public void toNetwork(IHotpotSoupFactory<HotpotSmeltingRecipeSoupType> factory, FriendlyByteBuf byteBuf) {
            HotpotSmeltingRecipeSoupType smeltingRecipeSoupType = factory.build();
            byteBuf.writeFloat(smeltingRecipeSoupType.waterLevelDropRate);
        }
    }
}
