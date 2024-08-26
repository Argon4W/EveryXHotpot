package com.github.argon4w.hotpot.soups.components;

import com.github.argon4w.hotpot.IHotpotResult;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.HotpotComponentSoupType;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HotpotRemoveSoupWhenCrouchingSoupComponent extends AbstractHotpotSoupComponent {
    @Override
    public IHotpotResult<Holder<IHotpotContentSerializer<?>>> getPlayerInteractionResult(int hitPos, Player player, InteractionHand hand, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<Holder<IHotpotContentSerializer<?>>> result) {
        if (result.isPresent()) {
            return result;
        }

        if (!itemStack.isEmpty()) {
            return result;
        }

        if (!player.isCrouching()) {
            return result;
        }

        if (!hotpotBlockEntity.canBeRemoved()) {
            return result;
        }

        hotpotBlockEntity.onRemove(pos);
        hotpotBlockEntity.setSoup(HotpotComponentSoupType.loadEmptySoup(), pos);

        return IHotpotResult.blocked();
    }
}
