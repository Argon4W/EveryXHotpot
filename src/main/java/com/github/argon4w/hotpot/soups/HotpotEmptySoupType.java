package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotContents;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.synchronizers.IHotpotSoupSynchronizer;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class HotpotEmptySoupType implements IHotpotSoupType {
    private final ResourceLocation resourceLocation;

    public HotpotEmptySoupType(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    @Override
    public IHotpotSoupType load(CompoundTag compoundTag) {
        return this;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        return compoundTag;
    }

    @Override
    public boolean isValid(CompoundTag compoundTag) {
        return true;
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }

    @Override
    public Optional<IHotpotContent> interact(int hitSection, Player player, InteractionHand hand, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos selfPos) {
        return Optional.empty();
    }

    @Override
    public Optional<IHotpotContent> remapContent(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return Optional.of(HotpotContents.getEmptyContent().build());
    }

    @Override
    public Optional<IHotpotContent> remapItemStack(boolean copy, ItemStack itemStack, LevelBlockPos pos) {
        return Optional.empty();
    }

    @Override
    public Optional<IHotpotSoupSynchronizer> getSynchronizer(HotpotBlockEntity selfHotpotBlockEntity, LevelBlockPos selfPos) {
        return Optional.empty();
    }

    @Override
    public ItemStack takeOutContentViaTableware(IHotpotContent content, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return itemStack;
    }

    @Override
    public void takeOutContentViaHand(IHotpotContent content, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        pos.dropItemStack(itemStack);
    }

    @Override
    public void contentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {

    }

    @Override
    public void animateTick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, RandomSource randomSource) {

    }

    @Override
    public float getWaterLevel(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return 0;
    }

    @Override
    public float getOverflowWaterLevel(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return 0f;
    }

    @Override
    public void setWaterLevel(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, float waterLevel) {

    }

    @Override
    public void discardOverflowWaterLevel(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {

    }

    @Override
    public int getContentTickSpeed(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return 0;
    }

    @Override
    public void entityInside(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, Entity entity) {

    }

    @Override
    public void tick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {

    }

    public record Factory(ResourceLocation resourceLocation) implements IHotpotSoupFactory<HotpotEmptySoupType> {
        @Override
        public HotpotEmptySoupType build() {
            return new HotpotEmptySoupType(resourceLocation);
        }

        @Override
        public IHotpotSoupTypeSerializer<HotpotEmptySoupType> getSerializer() {
            return HotpotSoupTypes.EMPTY_SOUP_SERIALIZER.get();
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return resourceLocation;
        }
    }

    public static class Serializer implements IHotpotSoupTypeSerializer<HotpotEmptySoupType> {
        @Override
        public Factory fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            return new Factory(resourceLocation);
        }

        @Override
        public Factory fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf byteBuf) {
            return new Factory(resourceLocation);
        }

        @Override
        public void toNetwork(IHotpotSoupFactory<HotpotEmptySoupType> factory, FriendlyByteBuf byteBuf) {

        }
    }
}
