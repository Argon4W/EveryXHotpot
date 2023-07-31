package com.github.argon4w.hotpot.soups;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

import java.util.Map;

public abstract class AbstractHotpotWaterBasedSoup extends AbstractEffectiveFluidBasedSoup {
    public AbstractHotpotWaterBasedSoup(){
        super(Map.of(
                (itemStack) -> itemStack.is(Items.WATER_BUCKET), new HotpotFluidRefill(1f, SoundEvents.BUCKET_EMPTY, () -> new ItemStack(Items.BUCKET)),
                (itemStack) -> itemStack.is(Items.POTION) && PotionUtils.getPotion(itemStack) == Potions.WATER, new HotpotFluidRefill(0.333f, SoundEvents.BOTTLE_FILL, () -> new ItemStack(Items.GLASS_BOTTLE))
        ));
    }
}
