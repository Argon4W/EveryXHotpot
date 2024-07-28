package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotContents;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.contents.IHotpotContentFactory;
import com.github.argon4w.hotpot.soups.synchronizers.IHotpotSoupSynchronizer;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class HotpotEmptySoupType implements IHotpotSoupType {
    private final ResourceLocation resourceLocation;

    public HotpotEmptySoupType(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }

    @Override
    public Optional<IHotpotContentFactory<?>> interact(int hitSection, Player player, InteractionHand hand, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos selfPos) {
        return Optional.empty();
    }

    @Override
    public Optional<IHotpotContentFactory<?>> remapItemStack(boolean copy, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return Optional.of(HotpotContents.getEmptyContentFactory());
    }

    @Override
    public List<IHotpotSoupSynchronizer> getSynchronizer(HotpotBlockEntity selfHotpotBlockEntity, LevelBlockPos selfPos) {
        return List.of();
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
    public float getWaterLevel() {
        return 0f;
    }

    @Override
    public float getOverflowWaterLevel() {
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

    @Override
    public boolean isHotpotLit(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return true;
    }

    public record Factory() implements IHotpotSoupTypeFactory<HotpotEmptySoupType> {
        @Override
        public MapCodec<HotpotEmptySoupType> buildFromCodec(ResourceLocation resourceLocation) {
            return MapCodec.unit(() -> new HotpotEmptySoupType(resourceLocation));
        }

        @Override
        public HotpotEmptySoupType buildFromScratch(ResourceLocation resourceLocation) {
            return new HotpotEmptySoupType(resourceLocation);
        }

        @Override
        public IHotpotSoupFactorySerializer<HotpotEmptySoupType> getSerializer() {
            return HotpotSoupTypes.EMPTY_SOUP_SERIALIZER.get();
        }
    }

    public static class Serializer implements IHotpotSoupFactorySerializer<HotpotEmptySoupType> {
        public static final StreamCodec<RegistryFriendlyByteBuf, Factory> STREAM_CODEC = StreamCodec.of((buffer, value) -> {}, buffer -> new Factory());
        public static final MapCodec<Factory> CODEC = MapCodec.unit(Factory::new);

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupTypeFactory<HotpotEmptySoupType>> getStreamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<? extends IHotpotSoupTypeFactory<HotpotEmptySoupType>> getCodec() {
            return CODEC;
        }
    }
}
