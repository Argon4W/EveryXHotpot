package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.LazyMapCodec;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;

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
        this.modelPartIndex = head ? 0 : HotpotPlayerContent.RANDOM_SOURCE.nextInt(VALID_PARTS.length);
    }

    @Override
    public ItemStack takeOut(Player player, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return modelPartIndex == 0? getPlayerHeadByProfile() : new ItemStack(Items.BONE, RANDOM_SOURCE.nextInt(0, 2));
    }

    @Override
    public void onOtherContentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {

    }

    @Override
    public boolean tick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return false;
    }

    @Override
    public boolean shouldRemove(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return false;
    }

    @Override
    public Holder<IHotpotContentFactory<?>> getContentFactoryHolder() {
        return HotpotContents.PLAYER_CONTENT;
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

    public static class Factory implements IHotpotContentFactory<HotpotPlayerContent> {
        public static final MapCodec<HotpotPlayerContent> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(content -> content.group(
                        ResolvableProfile.CODEC.fieldOf("Profile").forGetter(HotpotPlayerContent::getProfile),
                        Codec.INT.fieldOf("ModelPartIndex").forGetter(HotpotPlayerContent::getModelPartIndex)
                ).apply(content, HotpotPlayerContent::new))
        );

        @Override
        public HotpotPlayerContent buildFromItem(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity) {
            throw new IllegalStateException("Cannot built from itemStack");
        }

        @Override
        public MapCodec<HotpotPlayerContent> buildFromCodec() {
            return CODEC;
        }
    }
}
