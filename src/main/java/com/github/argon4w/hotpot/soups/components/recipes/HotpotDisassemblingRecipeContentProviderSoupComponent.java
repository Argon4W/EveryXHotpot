package com.github.argon4w.hotpot.soups.components.recipes;

import com.github.argon4w.hotpot.IHotpotResult;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotContentSerializers;
import com.github.argon4w.hotpot.contents.HotpotDisassemblingContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.components.AbstractHotpotSoupComponent;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;

public class HotpotDisassemblingRecipeContentProviderSoupComponent extends AbstractHotpotSoupComponent {
    @Override
    public IHotpotResult<Holder<IHotpotContentSerializer<?>>> getContentSerializerResultFromItemStack(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<Holder<IHotpotContentSerializer<?>>> result) {
        return result.isPresent() || itemStack.isEmpty() ? result : IHotpotResult.success(HotpotContentSerializers.DISASSEMBLING_RECIPE_CONTENT_SERIALIZER);
    }

    @Override
    public IHotpotResult<IHotpotContent> getContentResultByHand(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<IHotpotContent> result) {
        if (result.isEmpty()) {
            return result;
        }

        if (!(result.get() instanceof HotpotDisassemblingContent disassemblingContent)) {
            return result;
        }

        disassemblingContent.getDisassembledResultItemStacks(pos).forEach(pos::dropFloatingItemStack);
        return IHotpotResult.pass();
    }

    @Override
    public IHotpotResult<IHotpotContent> onContentUpdate(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<IHotpotContent> result) {
        if (result.isEmpty()) {
            return result;
        }

        if (!(result.get() instanceof HotpotDisassemblingContent disassemblingContent)) {
            return result;
        }

        if (disassemblingContent.hasDisassembledResult(pos)) {
            return result;
        }

        return IHotpotResult.blocked();
    }
}
