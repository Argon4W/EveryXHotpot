package com.github.argon4w.hotpot.soups.components.contents;

import com.github.argon4w.hotpot.IHotpotResult;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.components.AbstractHotpotSoupComponent;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HotpotAddItemStackContentByHandSoupComponent extends AbstractHotpotSoupComponent {
    @Override
    public IHotpotResult<Holder<IHotpotContentSerializer<?>>> getPlayerInteractionResult(int hitPos, Player player, InteractionHand hand, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<Holder<IHotpotContentSerializer<?>>> result) {
        if (result.isPresent()) {
            return result;
        }

        if (itemStack.isEmpty()) {
            return result;
        }

        return soup.getContentSerializerResultFromItemStack(itemStack, hotpotBlockEntity, pos);
    }
}
