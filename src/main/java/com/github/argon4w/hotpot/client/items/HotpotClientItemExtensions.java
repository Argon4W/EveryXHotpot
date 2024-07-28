package com.github.argon4w.hotpot.client.items;

import com.github.argon4w.hotpot.HotpotModEntry;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class HotpotClientItemExtensions implements IClientItemExtensions {
    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return HotpotModEntry.HOTPOT_SPECIAL_ITEM_RENDERER;
    }
}
