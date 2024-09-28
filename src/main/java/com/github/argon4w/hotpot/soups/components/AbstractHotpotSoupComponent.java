package com.github.argon4w.hotpot.soups.components;

import com.github.argon4w.hotpot.api.IHotpotResult;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.api.soups.components.IHotpotSoupComponent;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.api.contents.IHotpotContent;
import com.github.argon4w.hotpot.api.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.components.synchronizers.IHotpotSoupComponentSynchronizer;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public abstract class AbstractHotpotSoupComponent implements IHotpotSoupComponent {
    @Override
    public IHotpotResult<Holder<IHotpotContentSerializer<?>>> getPlayerInteractionResult(int position, Player player, InteractionHand hand, ItemStack itemStack, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<Holder<IHotpotContentSerializer<?>>> result, HotpotBlockEntity hotpotBlockEntity) {
        return result;
    }

    @Override
    public IHotpotResult<Holder<IHotpotContentSerializer<?>>> getContentSerializerResultFromItemStack(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<Holder<IHotpotContentSerializer<?>>> result) {
        return result;
    }

    @Override
    public IHotpotResult<IHotpotContent> getContentResultByTableware(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<IHotpotContent> result) {
        return result;
    }

    @Override
    public IHotpotResult<IHotpotContent> getContentResultByHand(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<IHotpotContent> result) {
        return result;
    }

    @Override
    public IHotpotResult<Double> getContentTickSpeed(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<Double> result) {
        return result;
    }

    @Override
    public IHotpotResult<Boolean> getHotpotLit(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<Boolean> result) {
        return result;
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
    public IHotpotResult<Double> onAwardExperience(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<Double> result) {
        return result;
    }

    @Override
    public IHotpotResult<IHotpotContent> onContentUpdate(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<IHotpotContent> result) {
        return result;
    }

    @Override
    public void onDiscardOverflowWaterLevel(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos) {

    }

    @Override
    public void onEntityInside(Entity entity, HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos) {

    }

    @Override
    public void onTick(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos) {

    }

    @Override
    public void setWaterLevel(double waterLevel, HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos) {

    }

    @Override
    public Optional<IHotpotSoupComponentSynchronizer> getSoupComponentSynchronizer(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos) {
        return Optional.empty();
    }

    @Override
    public boolean shouldSendToClient() {
        return false;
    }
}
