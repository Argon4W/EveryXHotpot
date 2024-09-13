package com.github.argon4w.hotpot.soups.components.recipes;

import com.github.argon4w.hotpot.IHotpotResult;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.*;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.components.AbstractHotpotSoupComponent;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;

public class HotpotPiglinBarterRecipeContentProviderSoupComponent extends AbstractHotpotSoupComponent {
    @Override
    public IHotpotResult<Holder<IHotpotContentSerializer<?>>> getContentSerializerResultFromItemStack(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<Holder<IHotpotContentSerializer<?>>> result) {
        return result.isPresent() || itemStack.isEmpty() ? result : IHotpotResult.success(HotpotContentSerializers.PIGLIN_BARTER_RECIPE_CONTENT_SERIALIZER);
    }
}
