package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotDisassemblingContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class HotpotDisassemblingRecipeSoupTypeRecipeSoupType extends AbstractHotpotFluidBasedSoupType {
    private final ResourceLocation resourceLocation;
    private final float waterLevelDropRate;

    public HotpotDisassemblingRecipeSoupTypeRecipeSoupType(ResourceLocation resourceLocation, float waterLevelDropRate) {
        this.resourceLocation = resourceLocation;
        this.waterLevelDropRate = waterLevelDropRate;
    }

    @Override
    public void animateTick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, RandomSource randomSource) {

    }

    @Override
    public Optional<IHotpotContent> remapItemStack(boolean copy, ItemStack itemStack, LevelBlockPos pos) {
        return Optional.of(new HotpotDisassemblingContent((copy ? itemStack.copy() : itemStack)));
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
        return false;
    }

    @Override
    public boolean canItemEnter(ItemEntity itemEntity) {
        return !itemEntity.hasPickUpDelay();
    }

    public record Factory(ResourceLocation resourceLocation, float waterLevelDropRate) implements IHotpotSoupFactory<HotpotDisassemblingRecipeSoupTypeRecipeSoupType> {
        @Override
        public HotpotDisassemblingRecipeSoupTypeRecipeSoupType build() {
            return new HotpotDisassemblingRecipeSoupTypeRecipeSoupType(resourceLocation, waterLevelDropRate);
        }

        @Override
        public IHotpotSoupTypeSerializer<HotpotDisassemblingRecipeSoupTypeRecipeSoupType> getSerializer() {
            return HotpotSoupTypes.DISASSEMBLING_RECIPE_SOUP_SERIALIZER.get();
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return resourceLocation;
        }
    }

    public static class Serializer implements IHotpotSoupTypeSerializer<HotpotDisassemblingRecipeSoupTypeRecipeSoupType> {
        @Override
        public Factory fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            if (!jsonObject.has("water_level_drop_rate")) {
                throw new JsonParseException("disassembling recipe soup must have a \"water_level_drop_rate\"");
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
        public void toNetwork(IHotpotSoupFactory<HotpotDisassemblingRecipeSoupTypeRecipeSoupType> factory, FriendlyByteBuf byteBuf) {
            HotpotDisassemblingRecipeSoupTypeRecipeSoupType disassemblingRecipeSoupType = factory.build();
            byteBuf.writeFloat(disassemblingRecipeSoupType.waterLevelDropRate);
        }
    }
}