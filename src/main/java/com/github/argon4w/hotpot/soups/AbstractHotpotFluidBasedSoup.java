package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.ItemUtils1201;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotCampfireRecipeContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.synchronizers.HotpotSoupActivenessSynchronizer;
import com.github.argon4w.hotpot.soups.synchronizers.IHotpotSoupSynchronizer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.util.Constants;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class AbstractHotpotFluidBasedSoup extends AbstractHotpotSoup implements IHotpotSoupWithActiveness {
    private final Map<Predicate<ItemStack>, HotpotFluidRefill> refills;
    private float activeness = 0f;

    public AbstractHotpotFluidBasedSoup(Map<Predicate<ItemStack>, HotpotFluidRefill> refills) {
        this.refills = refills;
    }

    @Override
    public IHotpotSoup load(CompoundNBT compoundTag) {
        activeness = compoundTag.getFloat("Activeness");

        return super.load(compoundTag);
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundTag) {
        compoundTag.putFloat("Activeness", activeness);

        return super.save(compoundTag);
    }

    @Override
    public boolean isValid(CompoundNBT compoundTag) {
        return super.isValid(compoundTag) && compoundTag.contains("Activeness", Constants.NBT.TAG_FLOAT);
    }

    @Override
    public void contentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        super.contentUpdate(content, hotpotBlockEntity, pos);

        if (content instanceof HotpotCampfireRecipeContent) {
            activeness = Math.min(1f, activeness + 0.025f * ((HotpotCampfireRecipeContent) content).getFoodProperties().map(Food::getNutrition).orElse(1));
        }
    }

    @Override
    public Optional<IHotpotContent> interact(int hitSection, PlayerEntity player, Hand hand, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel selfPos) {
        if (ifMatchedFluidFilled(itemStack, hotpotRefillReturnable -> {
            setWaterLevel(hotpotBlockEntity, selfPos, getWaterLevel(hotpotBlockEntity, selfPos) + hotpotRefillReturnable.waterLevel());

            player.setItemInHand(hand, ItemUtils1201.createFilledResult(itemStack, player, hotpotRefillReturnable.returned().get()));
            selfPos.level().playSound(null, selfPos.pos(), hotpotRefillReturnable.soundEvent(), SoundCategory.BLOCKS, 1.0F, 1.0F);
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
        setWaterLevel(getWaterLevel(hotpotBlockEntity, pos) - (hotpotBlockEntity.isInfiniteWater() ? 0 : getWaterLevelDropRate()) / 20f / 60f);
        activeness = Math.max(0f, activeness - 0.55f / 20f / 60f);
    }

    public abstract float getWaterLevelDropRate();

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

    public boolean ifMatchedFluidFilled(ItemStack itemStack, Consumer<HotpotFluidRefill> consumer) {
        Optional<Predicate<ItemStack>> key = refills.keySet().stream().filter(predicate -> predicate.test(itemStack)).findFirst();
        if (key.isPresent()) {
            consumer.accept(refills.get(key.get()));

            return true;
        }

        return false;
    }

    public static class HotpotFluidRefill {
        private final float waterLevel;
        private final SoundEvent soundEvent;
        private final Supplier<ItemStack> returned;

        public HotpotFluidRefill(float waterLevel, SoundEvent soundEvent, Supplier<ItemStack> returned) {
            this.waterLevel = waterLevel;
            this.soundEvent = soundEvent;
            this.returned = returned;
        }

        public float waterLevel() {
            return waterLevel;
        }

        public SoundEvent soundEvent() {
            return soundEvent;
        }

        public Supplier<ItemStack> returned() {
            return returned;
        }
    }
}
