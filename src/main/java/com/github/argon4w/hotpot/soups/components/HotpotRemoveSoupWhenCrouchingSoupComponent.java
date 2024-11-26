package com.github.argon4w.hotpot.soups.components;

import com.github.argon4w.fancytoys.blocks.LevelBlockPos;
import com.github.argon4w.hotpot.api.IHotpotResult;
import com.github.argon4w.hotpot.api.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.api.items.IHotpotTablewareInteraction;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HotpotRemoveSoupWhenCrouchingSoupComponent extends AbstractHotpotSoupComponent {

    @Override
    public IHotpotResult<Holder<IHotpotContentSerializer<?>>> getPlayerInteractionResult(
            IHotpotTablewareInteraction.Context context,
            ItemStack itemStack,
            IHotpotResult<Holder<IHotpotContentSerializer<?>>> result,
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup) {
        Player player = context.player();
        LevelBlockPos pos = context.pos();

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
        hotpotBlockEntity.setEmptySoup(pos);

        return IHotpotResult.blocked();
    }
}
