package com.github.argon4w.hotpot.soups.recipes.ingredients;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;

import java.util.List;

public record HotpotIngredientActionExecutor(List<HotpotIngredientActionContext> contexts, IHotpotSoupType resultSoup, float resultWaterLevel) {
    public void execute(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        contexts.forEach(context -> hotpotBlockEntity.setContent(context.slot(), context.action().action(pos, hotpotBlockEntity, hotpotBlockEntity.getContent(context.slot()), hotpotBlockEntity.getSoup(), resultSoup)));
        hotpotBlockEntity.setSoup(resultSoup, pos);
        hotpotBlockEntity.setWaterLevel(resultWaterLevel);
    }
}