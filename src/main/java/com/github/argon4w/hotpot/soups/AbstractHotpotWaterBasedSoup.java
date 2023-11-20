package com.github.argon4w.hotpot.soups;

import com.google.common.collect.ImmutableMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.SoundEvents;

import java.util.Map;

public abstract class AbstractHotpotWaterBasedSoup extends AbstractEffectiveFluidBasedSoup {
    public AbstractHotpotWaterBasedSoup(){
        super(ImmutableMap.of(
                (itemStack) -> itemStack.getItem().equals(Items.WATER_BUCKET), new HotpotFluidRefill(1f, SoundEvents.BUCKET_EMPTY, () -> new ItemStack(Items.BUCKET)),
                (itemStack) -> itemStack.getItem().equals(Items.POTION) && PotionUtils.getPotion(itemStack) == Potions.WATER, new HotpotFluidRefill(0.333f, SoundEvents.BOTTLE_FILL, () -> new ItemStack(Items.GLASS_BOTTLE))
        ));
    }

    @Override
    public float getWaterLevelDropRate() {
        return 0.04f;
    }
}
