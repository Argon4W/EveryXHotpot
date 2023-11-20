package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.ItemUtils1201;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotContents;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.renderers.IHotpotSoupCustomElementRenderer;
import com.github.argon4w.hotpot.soups.synchronizers.IHotpotSoupSynchronizer;
import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class HotpotEmptySoup implements IHotpotSoup {
    public static final HashMap<Predicate<ItemStack>, HotpotEmptyFill> HOTPOT_EMPTY_FILL_TYPES = new HashMap<>(ImmutableMap.of(
            (itemStack) -> itemStack.getItem().equals(Items.WATER_BUCKET), new HotpotEmptyFill(HotpotSoups.HOTPOT_SOUP_TYPES.get("ClearSoup"), 1f, SoundEvents.BUCKET_EMPTY, () -> new ItemStack(Items.BUCKET)),
            (itemStack) -> itemStack.getItem().equals(Items.POTION) && PotionUtils.getPotion(itemStack) == Potions.WATER, new HotpotEmptyFill(HotpotSoups.getSoupOrElseEmpty("ClearSoup"), 0.333f, SoundEvents.BOTTLE_FILL, () -> new ItemStack(Items.BUCKET)),
            (itemStack) -> itemStack.getItem().equals(Items.MILK_BUCKET), new HotpotEmptyFill(HotpotSoups.HOTPOT_SOUP_TYPES.get("CheeseSoup"), 1f, SoundEvents.BUCKET_EMPTY, () -> new ItemStack(Items.BUCKET)),
            (itemStack) -> itemStack.getItem().equals(Items.LAVA_BUCKET), new HotpotEmptyFill(HotpotSoups.HOTPOT_SOUP_TYPES.get("LavaSoup"), 1f, SoundEvents.BUCKET_EMPTY_LAVA, () -> new ItemStack(Items.BUCKET))
    ));

    @Override
    public IHotpotSoup load(CompoundNBT compoundTag) {
        return this;
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundTag) {
        return compoundTag;
    }

    @Override
    public boolean isValid(CompoundNBT compoundTag) {
        return true;
    }

    @Override
    public String getID() {
        return "Empty";
    }

    @Override
    public Optional<IHotpotContent> interact(int hitSection, PlayerEntity player, Hand hand, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel selfPos) {
        ifMatchEmptyFill(itemStack, returnable -> {
            player.setItemInHand(hand, ItemUtils1201.createFilledResult(itemStack, player, returnable.returned().get()));

            hotpotBlockEntity.setSoup(returnable.soup().get(), selfPos);
            hotpotBlockEntity.getSoup().setWaterLevel(hotpotBlockEntity, selfPos, returnable.waterLevel());

            selfPos.level().playSound(null, selfPos.pos(), returnable.soundEvent(), SoundCategory.BLOCKS, 1.0F, 1.0F);
        });

        return Optional.empty();
    }

    @Override
    public Optional<IHotpotContent> remapContent(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return Optional.of(HotpotContents.getEmptyContent().get());
    }

    @Override
    public Optional<IHotpotSoupSynchronizer> getSynchronizer(HotpotBlockEntity selfHotpotBlockEntity, BlockPosWithLevel selfPos) {
        return Optional.empty();
    }

    @Override
    public ItemStack takeOutContentViaChopstick(IHotpotContent content, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return itemStack;
    }

    @Override
    public void takeOutContentViaHand(IHotpotContent content, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        pos.dropItemStack(itemStack);
    }

    @Override
    public void contentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {

    }

    @Override
    public void animateTick(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos, Random randomSource) {

    }

    @Override
    public float getWaterLevel(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return 0;
    }

    @Override
    public float getOverflowWaterLevel(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return 0f;
    }

    @Override
    public void setWaterLevel(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos, float waterLevel) {

    }

    @Override
    public void discardOverflowWaterLevel(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {

    }

    @Override
    public int getContentTickSpeed(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return 0;
    }

    @Override
    public void entityInside(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos, Entity entity) {

    }

    @Override
    public void tick(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {

    }

    @Override
    public Optional<ResourceLocation> getBubbleResourceLocation() {
        return Optional.empty();
    }

    @Override
    public Optional<ResourceLocation> getSoupResourceLocation() {
        return Optional.empty();
    }

    @Override
    public List<IHotpotSoupCustomElementRenderer> getCustomElementRenderers() {
        return new ArrayList<>();
    }

    public static void ifMatchEmptyFill(ItemStack itemStack, Consumer<HotpotEmptyFill> consumer) {
        Optional<Predicate<ItemStack>> key = HotpotEmptySoup.HOTPOT_EMPTY_FILL_TYPES.keySet().stream().filter(predicate -> predicate.test(itemStack)).findFirst();
        key.ifPresent(itemStackPredicate -> consumer.accept(HotpotEmptySoup.HOTPOT_EMPTY_FILL_TYPES.get(itemStackPredicate)));
    }

    public static class HotpotEmptyFill {
        private final Supplier<IHotpotSoup> soup;
        private final float waterLevel;
        private final SoundEvent soundEvent;
        private final Supplier<ItemStack> returned;

        public HotpotEmptyFill(Supplier<IHotpotSoup> soup, float waterLevel, SoundEvent soundEvent, Supplier<ItemStack> returned) {
            this.soup = soup;
            this.waterLevel = waterLevel;
            this.soundEvent = soundEvent;
            this.returned = returned;
        }

        public Supplier<IHotpotSoup> soup() {
            return soup;
        }

        public float waterLevel() {
            return waterLevel;
        }

        public final SoundEvent soundEvent() {
            return soundEvent;
        }

        public Supplier<ItemStack> returned() {
            return returned;
        }
    }
}
