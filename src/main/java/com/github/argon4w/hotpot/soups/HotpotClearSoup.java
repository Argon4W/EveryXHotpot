package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotDefinitions;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotItemStackContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;

import java.util.Optional;

public class HotpotClearSoup extends AbstractHotpotSoup implements IHotpotSoupWithActiveness {
    private float activeness = 0f;

    @Override
    public String getID() {
        return "ClearSoup";
    }

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
    public Optional<IHotpotContent> interact(int hitSection, Player player, InteractionHand hand, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel selfPos) {
        if (HotpotDefinitions.ifMathClearSoupRefill(itemStack, hotpotRefillReturnable -> {
            setWaterLevel(hotpotBlockEntity, selfPos, getWaterLevel(hotpotBlockEntity, selfPos) + hotpotRefillReturnable.waterLevel());

            player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, hotpotRefillReturnable.returned().get()));
            selfPos.level().playSound(null, selfPos.pos(), hotpotRefillReturnable.soundEvent(), SoundSource.BLOCKS, 1.0F, 1.0F);
        })) {
            return Optional.empty();
        }

        return super.interact(hitSection, player, hand, itemStack, hotpotBlockEntity, selfPos);
    }

    @Override
    public void contentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        if (content instanceof HotpotItemStackContent itemStackContent) {
            activeness = Math.min(1f, activeness + 0.025f * itemStackContent.getFoodProperties().map(FoodProperties::getNutrition).orElse(1));
        }
    }

    @Override
    public void animateTick(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos, RandomSource randomSource) {

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

    @Override
    public Optional<ResourceLocation> getBubbleResourceLocation() {
        return Optional.of(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_clear_soup_bubble"));
    }

    @Override
    public Optional<ResourceLocation> getSoupResourceLocation() {
        return Optional.of(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_clear_soup"));
    }
}
