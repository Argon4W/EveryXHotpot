package com.github.argon4w.hotpot.soups.recipes.ingredients;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.IHotpotSoup;

import java.util.List;

public record HotpotIngredientActionExecutor(List<HotpotIngredientActionContext> contexts, IHotpotSoup resultSoup, float resultWaterLevel) {
    public void execute(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        contexts.forEach(context -> context.action().action(context.slot(), hotpotBlockEntity, hotpotBlockEntity.getContent(context.slot()), hotpotBlockEntity.getSoup(), resultSoup, pos));
        hotpotBlockEntity.setSoup(resultSoup, pos);
        hotpotBlockEntity.setWaterLevel(resultWaterLevel, pos);
    }
}