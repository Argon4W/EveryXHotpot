package com.github.argon4w.hotpot.soups.types;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotContentSerializers;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.soups.*;
import com.github.argon4w.hotpot.soups.synchronizers.IHotpotSoupSynchronizer;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class HotpotEmptySoup implements IHotpotSoup {
    private final HotpotSoupTypeHolder<?> soupTypeHolder;

    public HotpotEmptySoup(HotpotSoupTypeHolder<?> soupTypeHolder) {
        this.soupTypeHolder = soupTypeHolder;
    }

    @Override
    public Optional<IHotpotContentSerializer<?>> interact(int hitSection, Player player, InteractionHand hand, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos selfPos) {
        return Optional.empty();
    }

    @Override
    public Optional<IHotpotContentSerializer<?>> getContentSerializerFromItemStack(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return itemStack.isEmpty() ? Optional.empty() : Optional.of(HotpotContentSerializers.getEmptyContentSerializer());
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
    public void onContentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {

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
    public float getContentTickSpeed(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return 0.0f;
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

    @Override
    public HotpotSoupTypeHolder<?> getSoupTypeHolder() {
        return HotpotSoupTypeSerializers.getEmptySoupTypeHolder();
    }

    public record Type() implements IHotpotSoupType<HotpotEmptySoup> {
        @Override
        public MapCodec<HotpotEmptySoup> getCodec(HotpotSoupTypeHolder<HotpotEmptySoup> soupTypeHolder) {
            return MapCodec.unit(() -> new HotpotEmptySoup(soupTypeHolder));
        }

        @Override
        public HotpotEmptySoup getSoup(HotpotSoupTypeHolder<HotpotEmptySoup> soupTypeHolder) {
            return new HotpotEmptySoup(soupTypeHolder);
        }

        @Override
        public Holder<IHotpotSoupTypeSerializer<?>> getSerializer() {
            return HotpotSoupTypeSerializers.EMPTY_SOUP_TYPE_SERIALIZER;
        }
    }

    public static class Serializer implements IHotpotSoupTypeSerializer<HotpotEmptySoup> {
        public static final StreamCodec<RegistryFriendlyByteBuf, Type> STREAM_CODEC = StreamCodec.of((buffer, value) -> {}, buffer -> new Type());
        public static final MapCodec<Type> CODEC = MapCodec.unit(Type::new);

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupType<HotpotEmptySoup>> getStreamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<? extends IHotpotSoupType<HotpotEmptySoup>> getCodec() {
            return CODEC;
        }
    }
}
