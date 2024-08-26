package com.github.argon4w.hotpot.soups.components;

import com.github.argon4w.hotpot.IHotpotResult;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import org.joml.Math;

public class HotpotSummonExperienceOrbSoupComponent extends AbstractHotpotSoupComponent {
    @Override
    public IHotpotResult<Double> onAwardExperience(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<Double> result) {
        return result.isPresent() && pos.level() instanceof ServerLevel serverLevel ? result.consume(experience -> ExperienceOrb.award(serverLevel, pos.toVec3(), (int) Math.ceil(experience))) : result;
    }
}
