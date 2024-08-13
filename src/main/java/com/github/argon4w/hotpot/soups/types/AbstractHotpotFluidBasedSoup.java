package com.github.argon4w.hotpot.soups.types;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotCookingRecipeContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.soups.HotpotSoupTypeHolder;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import com.github.argon4w.hotpot.soups.recipes.HotpotSoupRechargeRecipe;
import com.github.argon4w.hotpot.soups.recipes.input.HotpotRecipeInput;
import com.github.argon4w.hotpot.soups.synchronizers.HotpotSoupActivenessSynchronizer;
import com.github.argon4w.hotpot.soups.synchronizers.IHotpotSoupSynchronizer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractHotpotFluidBasedSoup extends AbstractHotpotSoup implements IHotpotSoupWithActiveness {
    public static final RecipeManager.CachedCheck<HotpotRecipeInput, HotpotSoupRechargeRecipe> RECHARGE_RECIPE_QUICK_CHECK = RecipeManager.createCheck(HotpotModEntry.HOTPOT_SOUP_RECHARGE_RECIPE_TYPE.get());

    protected float activeness = 0f;

    public abstract float getWaterLevelDropRate();

    @Override
    public boolean canItemEnter(ItemEntity itemEntity) {
        return true;
    }

    @Override
    public void onContentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        super.onContentUpdate(content, hotpotBlockEntity, pos);

        if (!(content instanceof HotpotCookingRecipeContent itemStackContent)) {
            return;
        }

        int nutrition = itemStackContent.getItemStack().has(DataComponents.FOOD) ? itemStackContent.getItemStack().get(DataComponents.FOOD).nutrition() : 1;
        activeness = Math.min(1f, activeness + 0.025f * nutrition);
    }

    @Override
    public Optional<IHotpotContentSerializer<?>> interact(int hitPos, Player player, InteractionHand hand, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos selfPos) {
        Optional<HotpotSoupRechargeRecipe> optional = RECHARGE_RECIPE_QUICK_CHECK.getRecipeFor(new HotpotRecipeInput(itemStack, hotpotBlockEntity.getSoup()), selfPos.level()).map(RecipeHolder::value);

        if (optional.isPresent()) {
            HotpotSoupRechargeRecipe recipe = optional.get();
            setWaterLevel(hotpotBlockEntity, selfPos, getWaterLevel() + recipe.getRechargeWaterLevel());
            player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, recipe.getRemainingItem()));
            selfPos.playSound(recipe.getSoundEvent());

            return Optional.empty();
        }

        return super.interact(hitPos, player, hand, itemStack, hotpotBlockEntity, selfPos);
    }

    @Override
    public float getContentTickSpeed(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return getWaterLevel() * 2f + activeness * 4f;
    }

    @Override
    public void tick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        setWaterLevel(getWaterLevel() - (hotpotBlockEntity.isInfiniteWater() ? 0 : getWaterLevelDropRate()) / 20f / 60f);
        activeness = Math.max(0f, activeness - 0.55f / 20f / 60f);
    }

    @Override
    public List<IHotpotSoupSynchronizer> getSynchronizers(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return contact(super.getSynchronizers(hotpotBlockEntity, pos), new HotpotSoupActivenessSynchronizer());
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

    public static abstract class Type<T extends AbstractHotpotFluidBasedSoup> implements IHotpotSoupType<T> {
        public abstract T buildFrom(HotpotSoupTypeHolder<T> soupTypeHolder, float waterLevel, float overflowWaterLevel, float activeness);

        @Override
        public MapCodec<T> getCodec(HotpotSoupTypeHolder<T> soupTypeHolder) {
            return RecordCodecBuilder.mapCodec(soupType -> soupType.group(
                    Codec.FLOAT.fieldOf("waterlevel").forGetter(AbstractHotpotSoup::getWaterLevel),
                    Codec.FLOAT.fieldOf("overflow_waterlevel").forGetter(AbstractHotpotSoup::getOverflowWaterLevel),
                    Codec.FLOAT.fieldOf("activeness").forGetter(AbstractHotpotFluidBasedSoup::getActiveness)
            ).apply(soupType, (waterLevel, overflowWaterLevel, activeness) -> buildFrom(soupTypeHolder, waterLevel, overflowWaterLevel, activeness)));
        }
    }
}
