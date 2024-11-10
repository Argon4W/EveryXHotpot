package com.github.argon4w.hotpot.api.blocks;

import com.github.argon4w.hotpot.api.items.IHotpotTablewareInteraction;
import net.minecraft.Util;
import net.minecraft.world.item.ItemStack;

public interface IHotpotTablewareContainer extends IHotpotTablewareInteraction {

    @Override
    default void interact(Context context, ItemStack itemStack, IHotpotTablewareContainer blockEntity) {
        setContentByInteraction(context, itemStack);
    }

    default void interact(Context context, ItemStack itemStack) {
        getInteraction(itemStack).interact(context, itemStack, this);
    }

    default ItemStack setContentByTableware(Context context, ItemStack itemStack) {
        return Util.make(itemStack, itemStack1 -> setContentByInteraction(context, itemStack1));
    }

    default IHotpotTablewareInteraction getInteraction(ItemStack itemStack) {
        return (itemStack.getItem() instanceof IHotpotTablewareInteraction interaction ? interaction : this);
    }

    void setContentByInteraction(Context context, ItemStack itemStack);
    ItemStack getContentByTableware(Context context);
}
