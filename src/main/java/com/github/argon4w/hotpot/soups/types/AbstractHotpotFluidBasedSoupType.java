package com.github.argon4w.hotpot.soups.types;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotCookingRecipeContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.contents.IHotpotContentFactory;
import com.github.argon4w.hotpot.soups.HotpotSoupTypeFactoryHolder;
import com.github.argon4w.hotpot.soups.IHotpotSoupTypeFactory;
import com.github.argon4w.hotpot.soups.recipes.HotpotSoupRechargeRecipe;
import com.github.argon4w.hotpot.soups.synchronizers.HotpotSoupActivenessSynchronizer;
import com.github.argon4w.hotpot.soups.synchronizers.IHotpotSoupSynchronizer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractHotpotFluidBasedSoupType extends AbstractHotpotSoupType implements IHotpotSoupTypeWithActiveness {
    protected float activeness = 0f;

    public abstract float getWaterLevelDropRate();

    @Override
    public boolean canItemEnter(ItemEntity itemEntity) {
        return true;
    }

    @Override
    public void contentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        super.contentUpdate(content, hotpotBlockEntity, pos);

        if (!(content instanceof HotpotCookingRecipeContent itemStackContent)) {
            return;
        }

        FoodProperties foodProperties = itemStackContent.getItemStack().get(DataComponents.FOOD);
        int nutrition = foodProperties == null ? 1 : foodProperties.nutrition();
        activeness = Math.min(1f, activeness + 0.025f * nutrition);
    }

    @Override
    public Optional<IHotpotContentFactory<?>> interact(int hitPos, Player player, InteractionHand hand, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos selfPos) {
        for (RecipeHolder<HotpotSoupRechargeRecipe> holder : selfPos.level().getRecipeManager().getAllRecipesFor(HotpotModEntry.HOTPOT_SOUP_RECHARGE_RECIPE_TYPE.get())) {
            HotpotSoupRechargeRecipe recipe = holder.value();

            if (!recipe.matches(itemStack, hotpotBlockEntity.getSoup())) {
                continue;
            }

            setWaterLevel(hotpotBlockEntity, selfPos, getWaterLevel() + recipe.getRechargeWaterLevel());
            player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, recipe.getRemainingItem()));
            selfPos.playSound(recipe.getSoundEvent());

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
        setWaterLevel(getWaterLevel() - (hotpotBlockEntity.isInfiniteWater() ? 0 : getWaterLevelDropRate()) / 20f / 60f);
        activeness = Math.max(0f, activeness - 0.55f / 20f / 60f);
    }

    @Override
    public List<IHotpotSoupSynchronizer> getSynchronizer(HotpotBlockEntity selfHotpotBlockEntity, LevelBlockPos selfPos) {
        return contact(super.getSynchronizer(selfHotpotBlockEntity, selfPos), new HotpotSoupActivenessSynchronizer());
    }

    @Override
    public float getActiveness() {
        return activeness;
    }

    @Override
    public void setActiveness(float activeness) {
        this.activeness = Math.min(1f, Math.max(0f, activeness));
    }

    public <T> List<T> contact(List<T> list, T element) {
        list = new ArrayList<>(list);
        list.add(element);
        return list;
    }

    public static abstract class Factory<T extends AbstractHotpotFluidBasedSoupType> implements IHotpotSoupTypeFactory<T> {
        public abstract T buildFrom(HotpotSoupTypeFactoryHolder<T> soupTypeFactoryHolder, float waterLevel, float overflowWaterLevel, float activeness);

        @Override
        public MapCodec<T> buildFromCodec(HotpotSoupTypeFactoryHolder<T> soupTypeFactoryHolder) {
            return RecordCodecBuilder.mapCodec(soupType -> soupType.group(
                    Codec.FLOAT.fieldOf("WaterLevel").forGetter(AbstractHotpotSoupType::getWaterLevel),
                    Codec.FLOAT.fieldOf("OverflowWaterLevel").forGetter(AbstractHotpotSoupType::getOverflowWaterLevel),
                    Codec.FLOAT.fieldOf("Activeness").forGetter(AbstractHotpotFluidBasedSoupType::getActiveness)
            ).apply(soupType, (waterLevel, overflowWaterLevel, activeness) -> buildFrom(soupTypeFactoryHolder, waterLevel, overflowWaterLevel, activeness)));
        }
    }
}
