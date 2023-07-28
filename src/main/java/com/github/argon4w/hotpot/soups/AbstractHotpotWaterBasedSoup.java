package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotItemStackContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.effects.HotpotEffectHelper;
import com.github.argon4w.hotpot.soups.synchronizers.HotpotSoupActivenessSynchronizer;
import com.github.argon4w.hotpot.soups.synchronizers.IHotpotSoupSynchronizer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class AbstractHotpotWaterBasedSoup extends AbstractHotpotSoup implements IHotpotSoupWithActiveness {
    public static final ConcurrentHashMap<Predicate<ItemStack>, HotpotWaterBasedRefill> HOTPOT_CLEAR_SOUP_REFILL_TYPES = new ConcurrentHashMap<>(Map.of(
            (itemStack) -> itemStack.is(Items.WATER_BUCKET), new HotpotWaterBasedRefill(1f, SoundEvents.BUCKET_EMPTY, () -> new ItemStack(Items.BUCKET)),
            (itemStack) -> itemStack.is(Items.POTION) && PotionUtils.getPotion(itemStack) == Potions.WATER, new HotpotWaterBasedRefill(0.333f, SoundEvents.BOTTLE_FILL, () -> new ItemStack(Items.GLASS_BOTTLE))
    ));

    private float activeness = 0f;

    @Override
    public IHotpotSoup load(CompoundTag compoundTag) {
        activeness = compoundTag.getFloat("Activeness");

        return super.load(compoundTag);
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.putFloat("Activeness", activeness);

        return super.save(compoundTag);
    }

    @Override
    public boolean isValid(CompoundTag compoundTag) {
        return super.isValid(compoundTag) && compoundTag.contains("Activeness", Tag.TAG_FLOAT);
    }

    @Override
    public void contentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        super.contentUpdate(content, hotpotBlockEntity, pos);

        if (content instanceof HotpotItemStackContent itemStackContent) {
            activeness = Math.min(1f, activeness + 0.025f * itemStackContent.getFoodProperties().map(FoodProperties::getNutrition).orElse(1));
        }
    }

    @Override
    public Optional<IHotpotContent> interact(int hitSection, Player player, InteractionHand hand, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel selfPos) {
        if (ifMatchedWaterBasedFilled(itemStack, hotpotRefillReturnable -> {
            setWaterLevel(hotpotBlockEntity, selfPos, getWaterLevel(hotpotBlockEntity, selfPos) + hotpotRefillReturnable.waterLevel());

            player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, hotpotRefillReturnable.returned().get()));
            selfPos.level().playSound(null, selfPos.pos(), hotpotRefillReturnable.soundEvent(), SoundSource.BLOCKS, 1.0F, 1.0F);
        })) {
            return Optional.empty();
        }

        return super.interact(hitSection, player, hand, itemStack, hotpotBlockEntity, selfPos);
    }

    @Override
    public int getContentTickSpeed(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return Math.round(2f * (getWaterLevel() * 2f - 1f) + activeness * 4f);
    }

    @Override
    public void tick(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        setWaterLevel(getWaterLevel(hotpotBlockEntity, pos) - 0.5f / 20f / 60f);
        activeness = Math.max(0f, activeness - 0.55f / 20f / 60f);
    }

    @Override
    public ItemStack takeOutContentViaChopstick(IHotpotContent content, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        ItemStack result = super.takeOutContentViaChopstick(content, itemStack, hotpotBlockEntity, pos);

        if (content instanceof HotpotItemStackContent itemStackContent && itemStackContent.getFoodProperties().isPresent()) {
            addEffectToItem(itemStack, hotpotBlockEntity, pos);
        }

        return result;
    }

    public abstract void addEffectToItem(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);

    @Override
    public Optional<IHotpotSoupSynchronizer> getSynchronizer(HotpotBlockEntity selfHotpotBlockEntity, BlockPosWithLevel selfPos) {
        return super.getSynchronizer(selfHotpotBlockEntity, selfPos).orElse(IHotpotSoupSynchronizer.empty()).andThen(new HotpotSoupActivenessSynchronizer()).ofOptional();
    }

    @Override
    public float getActiveness(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return activeness;
    }

    @Override
    public void setActiveness(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos, float activeness) {
        this.activeness = Math.min(1f, Math.max(0f, activeness));
    }

    public static boolean ifMatchedWaterBasedFilled(ItemStack itemStack, Consumer<HotpotWaterBasedRefill> consumer) {
        Optional<Predicate<ItemStack>> key = AbstractHotpotWaterBasedSoup.HOTPOT_CLEAR_SOUP_REFILL_TYPES.keySet().stream().filter(predicate -> predicate.test(itemStack)).findFirst();
        if (key.isPresent()) {
            consumer.accept(AbstractHotpotWaterBasedSoup.HOTPOT_CLEAR_SOUP_REFILL_TYPES.get(key.get()));

            return true;
        }

        return false;
    }

    public record HotpotWaterBasedRefill(float waterLevel, SoundEvent soundEvent, Supplier<ItemStack> returned) {}
}
