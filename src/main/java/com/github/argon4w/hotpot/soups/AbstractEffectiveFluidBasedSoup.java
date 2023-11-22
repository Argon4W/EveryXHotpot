package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotCampfireRecipeContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class AbstractEffectiveFluidBasedSoup  extends AbstractHotpotFluidBasedSoup {

    public AbstractEffectiveFluidBasedSoup(Map<Predicate<ItemStack>, HotpotFluidRefill> refills) {
        super(refills);
    }

    @Override
    public ItemStack takeOutContentViaChopstick(IHotpotContent content, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        ItemStack result = super.takeOutContentViaChopstick(content, itemStack, hotpotBlockEntity, pos);

        if (content instanceof HotpotCampfireRecipeContent itemStackContent && itemStackContent.getFoodProperties().isPresent() && itemStackContent.getCookingTime() < 0) {
            addEffectToItem(itemStack, hotpotBlockEntity, pos);
        }

        return result;
    }

    public abstract void addEffectToItem(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);

    @Override
    public Optional<IHotpotContent> remapItemStack(boolean copy, ItemStack itemStack, BlockPosWithLevel pos) {
        return Optional.of(new HotpotCampfireRecipeContent((copy ? itemStack.copy() : itemStack)));
    }
}