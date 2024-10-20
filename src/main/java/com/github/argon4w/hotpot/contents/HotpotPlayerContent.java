package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.api.contents.AbstractHotpotRotatingContentSerializer;
import com.github.argon4w.hotpot.api.contents.IHotpotContent;
import com.github.argon4w.hotpot.api.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.codecs.LazyMapCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;

import java.util.List;

public class HotpotPlayerContent implements IHotpotContent {
    public static final String[] VALID_PARTS = {"head", "body", "right_arm", "left_arm", "right_leg", "left_leg"};
    public static final RandomSource RANDOM_SOURCE = RandomSource.createNewThreadLocalInstance();

    private final ResolvableProfile profile;
    private final int modelPartIndex;

    public HotpotPlayerContent(ResolvableProfile profile, int modelPartIndex) {
        this.profile = profile;
        this.modelPartIndex = modelPartIndex;
    }

    public HotpotPlayerContent(ResolvableProfile profile, boolean head) {
        this.profile = profile;
        this.modelPartIndex = head ? 0 : HotpotPlayerContent.RANDOM_SOURCE.nextInt(1, VALID_PARTS.length);
    }

    @Override
    public ItemStack getContentItemStack(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return modelPartIndex == 0? getPlayerHeadByProfile() : new ItemStack(Items.BONE, RANDOM_SOURCE.nextInt(0, 2));
    }

    @Override
    public List<ItemStack> getContentResultItemStacks(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return List.of(getContentItemStack(hotpotBlockEntity, pos));
    }

    @Override
    public void onContentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {

    }

    @Override
    public boolean onTick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, double ticks) {
        return false;
    }

    @Override
    public boolean shouldRemove(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return false;
    }

    @Override
    public Holder<IHotpotContentSerializer<?>> getContentSerializerHolder() {
        return HotpotContentSerializers.PLAYER_CONTENT_SERIALIZER;
    }

    public ResolvableProfile getProfile() {
        return profile;
    }

    public int getModelPartIndex() {
        return modelPartIndex;
    }

    public ItemStack getPlayerHeadByProfile() {
        ItemStack itemStack = new ItemStack(Items.PLAYER_HEAD);
        itemStack.set(DataComponents.PROFILE, profile);

        return itemStack;
    }

    public static class Serializer extends AbstractHotpotRotatingContentSerializer<HotpotPlayerContent> {
        public static final MapCodec<HotpotPlayerContent> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(content -> content.group(
                        ResolvableProfile.CODEC.fieldOf("profile").forGetter(HotpotPlayerContent::getProfile),
                        Codec.INT.fieldOf("model_part_index").forGetter(HotpotPlayerContent::getModelPartIndex)
                ).apply(content, HotpotPlayerContent::new))
        );

        @Override
        public HotpotPlayerContent createContent(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, Direction direction) {
            throw new IllegalStateException("Illegal call to a non-item based content");
        }

        @Override
        public MapCodec<HotpotPlayerContent> getCodec() {
            return CODEC;
        }
    }
}
