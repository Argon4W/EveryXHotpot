package com.github.argon4w.hotpot.soups.components.contents;

import com.github.argon4w.hotpot.api.IHotpotResult;
import com.github.argon4w.hotpot.api.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.api.contents.IHotpotPickableContent;
import com.github.argon4w.hotpot.api.items.IHotpotTablewareInteraction;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.components.AbstractHotpotSoupComponent;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;

public class HotpotTryPickContentByHandSoupComponent extends AbstractHotpotSoupComponent {

    @Override
    public IHotpotResult<Holder<IHotpotContentSerializer<?>>> getPlayerInteractionResult(
            IHotpotTablewareInteraction.Context context,
            ItemStack itemStack,
            IHotpotResult<Holder<IHotpotContentSerializer<?>>> result,
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup) {
        if (result.isPresent()) {
            return result;
        }

        if (!itemStack.isEmpty()) {
            return result;
        }

        if (!(hotpotBlockEntity.getContentAtPosition(context.position()) instanceof IHotpotPickableContent)) {
            return result;
        }

        hotpotBlockEntity.pickContentByHand(context);

        return IHotpotResult.blocked();
    }
}
