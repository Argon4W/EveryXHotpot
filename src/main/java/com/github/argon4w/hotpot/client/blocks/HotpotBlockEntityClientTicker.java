package com.github.argon4w.hotpot.client.blocks;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.client.soups.HotpotSoupRendererConfigManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class HotpotBlockEntityClientTicker {
    public static void tick(Level level, BlockPos pos, BlockState state, HotpotBlockEntity hotpotBlockEntity) {
        LevelBlockPos selfPos = new LevelBlockPos(level, pos);
        HotpotSoupRendererConfigManager.getSoupRendererConfig(hotpotBlockEntity.getSoup().getSoupTypeHolder().key()).clientTickEffects().forEach(effect -> effect.tick(selfPos, hotpotBlockEntity));
    }
}
