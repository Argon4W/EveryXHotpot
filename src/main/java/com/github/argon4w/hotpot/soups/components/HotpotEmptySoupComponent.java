package com.github.argon4w.hotpot.soups.components;

import com.github.argon4w.fancytoys.LevelBlockPos;
import com.github.argon4w.hotpot.api.IHotpotResult;
import com.github.argon4w.hotpot.api.contents.IHotpotContent;
import com.github.argon4w.hotpot.api.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.api.items.IHotpotTablewareInteraction;
import com.github.argon4w.hotpot.api.soups.components.IHotpotSoupComponent;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.components.synchronizers.IHotpotSoupSyncData;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class HotpotEmptySoupComponent implements IHotpotSoupComponent {

    @Override
    public IHotpotResult<Holder<IHotpotContentSerializer<?>>> getPlayerInteractionResult(
            IHotpotTablewareInteraction.Context context,
            ItemStack itemStack,
            IHotpotResult<Holder<IHotpotContentSerializer<?>>> result,
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup) {
        return result;
    }

    @Override
    public IHotpotResult<Holder<IHotpotContentSerializer<?>>> getContentSerializerResultFromItemStack(
            ItemStack itemStack,
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos,
            IHotpotResult<Holder<IHotpotContentSerializer<?>>> result) {
        return result;
    }

    @Override
    public IHotpotResult<IHotpotContent> getContentResultByTableware(
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos,
            IHotpotResult<IHotpotContent> result) {
        return result;
    }

    @Override
    public IHotpotResult<IHotpotContent> getContentResultByHand(
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos,
            IHotpotResult<IHotpotContent> result) {
        return result;
    }

    @Override
    public IHotpotResult<Double> getContentTickSpeed(
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos,
            IHotpotResult<Double> result) {
        return result;
    }

    @Override
    public IHotpotResult<Boolean> getHotpotLit(
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos,
            IHotpotResult<Boolean> result) {
        return result;
    }

    @Override
    public IHotpotResult<Double> onAwardExperience(
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos,
            IHotpotResult<Double> result) {
        return result;
    }

    @Override
    public IHotpotResult<IHotpotContent> onContentUpdate(
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos,
            IHotpotResult<IHotpotContent> result) {
        return result;
    }

    @Override
    public void onDiscardOverflowWaterLevel(
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos) {

    }

    @Override
    public void onEntityInside(
            Entity entity,
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos) {

    }

    @Override
    public void onTick(
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos) {

    }

    @Override
    public void setWaterLevelWithOverflow(
            double waterLevel,
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos) {

    }

    @Override
    public void setWaterLevel(
            double waterLevel,
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos) {

    }

    @Override
    public Optional<IHotpotSoupSyncData> getSoupComponenentSyncData(
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos) {
        return Optional.empty();
    }

    @Override
    public IHotpotResult<Double> getWaterLevel(IHotpotResult<Double> result) {
        return result;
    }

    @Override
    public IHotpotResult<Double> getOverflowWaterLevel(IHotpotResult<Double> result) {
        return result;
    }

    @Override
    public boolean shouldSendToClient() {
        return false;
    }
}
