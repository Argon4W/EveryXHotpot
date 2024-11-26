package com.github.argon4w.hotpot.api.soups.components;

import com.github.argon4w.fancytoys.blocks.LevelBlockPos;
import com.github.argon4w.hotpot.api.IHotpotResult;
import com.github.argon4w.hotpot.api.contents.IHotpotContent;
import com.github.argon4w.hotpot.api.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.api.items.IHotpotTablewareInteraction;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.components.synchronizers.IHotpotSoupSyncData;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public interface IHotpotSoupComponent {

    IHotpotResult<Holder<IHotpotContentSerializer<?>>> getPlayerInteractionResult(
            IHotpotTablewareInteraction.Context context,
            ItemStack itemStack,
            IHotpotResult<Holder<IHotpotContentSerializer<?>>> result,
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup);

    IHotpotResult<Holder<IHotpotContentSerializer<?>>> getContentSerializerResultFromItemStack(
            ItemStack itemStack,
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos,
            IHotpotResult<Holder<IHotpotContentSerializer<?>>> result);

    IHotpotResult<IHotpotContent> getContentResultByTableware(
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos,
            IHotpotResult<IHotpotContent> result);

    IHotpotResult<IHotpotContent> getContentResultByHand(
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos,
            IHotpotResult<IHotpotContent> result);

    IHotpotResult<Double> getContentTickSpeed(
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos,
            IHotpotResult<Double> result);

    IHotpotResult<Boolean> getHotpotLit(
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos,
            IHotpotResult<Boolean> result);

    IHotpotResult<Double> onAwardExperience(
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos,
            IHotpotResult<Double> result);

    IHotpotResult<IHotpotContent> onContentUpdate(
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos,
            IHotpotResult<IHotpotContent> result);

    void setWaterLevelWithOverflow(
            double waterLevel,
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos);

    void setWaterLevel(
            double waterLevel,
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos);

    Optional<IHotpotSoupSyncData> getSoupComponenentSyncData(
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos);

    void onEntityInside(
            Entity entity,
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos);

    void onDiscardOverflowWaterLevel(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos);
    void onTick(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos);
    IHotpotResult<Double> getWaterLevel(IHotpotResult<Double> result);
    IHotpotResult<Double> getOverflowWaterLevel(IHotpotResult<Double> result);
    boolean shouldSendToClient();
}
