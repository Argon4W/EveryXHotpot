package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotCookingRecipeContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.recipes.HotpotSoupRechargeRecipe;
import com.github.argon4w.hotpot.soups.synchronizers.HotpotSoupActivenessSynchronizer;
import com.github.argon4w.hotpot.soups.synchronizers.IHotpotSoupSynchronizer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractHotpotFluidBasedSoupType extends AbstractHotpotSoupType implements IHotpotSoupTypeWithActiveness {
    private float activeness = 0f;

    public abstract float getWaterLevelDropRate();

    @Override
    public boolean canItemEnter(ItemEntity itemEntity) {
        return true;
    }

    @Override
    public IHotpotSoupType load(CompoundTag compoundTag) {
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
    public void contentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        super.contentUpdate(content, hotpotBlockEntity, pos);

        if (!(content instanceof HotpotCookingRecipeContent itemStackContent)) {
            return;
        }

        FoodProperties foodProperties = itemStackContent.getItemStack().getFoodProperties(null);
        int nutrition = foodProperties == null ? 1 : foodProperties.getNutrition();
        activeness = Math.min(1f, activeness + 0.025f * nutrition);
    }

    @Override
    public Optional<IHotpotContent> interact(int hitPos, Player player, InteractionHand hand, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos selfPos) {
        for (HotpotSoupRechargeRecipe recipe : selfPos.level().getRecipeManager().getAllRecipesFor(HotpotModEntry.HOTPOT_SOUP_RECHARGE_RECIPE_TYPE.get())) {
            if (!recipe.matches(itemStack)) {
                continue;
            }

            if (!hotpotBlockEntity.getSoup().getResourceLocation().equals(recipe.getTargetSoup())) {
                continue;
            }

            setWaterLevel(hotpotBlockEntity, selfPos, getWaterLevel(hotpotBlockEntity, selfPos) + recipe.getRechargeWaterLevel());
            player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, recipe.getRemainingItem()));

            SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(recipe.getSoundEvent());
            selfPos.level().playSound(null, selfPos.pos(), soundEvent == null ? SoundEvents.EMPTY : soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);

            return Optional.empty();
        }

        return super.interact(hitPos, player, hand, itemStack, hotpotBlockEntity, selfPos);
    }

    @Override
    public int getContentTickSpeed(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return Math.round(2f * (getWaterLevel() * 2f - 1f) + activeness * 4f);
    }

    @Override
    public void tick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        setWaterLevel(getWaterLevel(hotpotBlockEntity, pos) - (hotpotBlockEntity.isInfiniteWater() ? 0 : getWaterLevelDropRate()) / 20f / 60f);
        activeness = Math.max(0f, activeness - 0.55f / 20f / 60f);
    }

    @Override
    public List<IHotpotSoupSynchronizer> getSynchronizer(HotpotBlockEntity selfHotpotBlockEntity, LevelBlockPos selfPos) {
        return contact(super.getSynchronizer(selfHotpotBlockEntity, selfPos), new HotpotSoupActivenessSynchronizer());
    }

    @Override
    public float getActiveness(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return activeness;
    }

    @Override
    public void setActiveness(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, float activeness) {
        this.activeness = Math.min(1f, Math.max(0f, activeness));
    }

    public <T> List<T> contact(List<T> list, T element) {
        list = new ArrayList<>(list);
        list.add(element);
        return list;
    }
}
