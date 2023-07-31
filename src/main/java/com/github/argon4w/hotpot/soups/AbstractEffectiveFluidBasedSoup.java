package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotItemStackContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

import java.util.Map;
import java.util.function.Predicate;

public abstract class AbstractEffectiveFluidBasedSoup  extends AbstractHotpotFluidBasedSoup {

    public AbstractEffectiveFluidBasedSoup(Map<Predicate<ItemStack>, HotpotFluidRefill> refills) {
        super(refills);
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
    public IHotpotContent remapItemStack(boolean copy, ItemStack itemStack) {
        return new HotpotItemStackContent((copy ? itemStack.copy() : itemStack));
    }
}