package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.EntryStreams;
import com.github.argon4w.hotpot.api.IHotpotResult;
import com.github.argon4w.hotpot.IndexHolder;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.api.contents.IHotpotContent;
import com.github.argon4w.hotpot.api.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.api.soups.components.IHotpotSoupComponent;
import com.github.argon4w.hotpot.api.soups.components.IHotpotSoupComponentTypeSerializer;
import com.github.argon4w.hotpot.soups.components.synchronizers.IHotpotSoupComponentSynchronizer;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public record HotpotComponentSoup(LinkedHashMap<ResourceLocation, IndexHolder<IHotpotSoupComponent>> components, Holder<HotpotComponentSoupType> soupTypeHolder) {
    @SuppressWarnings("unchecked")
    public <T extends IHotpotSoupComponent> List<Pair<ResourceLocation, T>> getComponentPairsByTypes(List<Supplier<? extends IHotpotSoupComponentTypeSerializer<? extends T>>> componentTypeSerializerHolders) {
        return soupTypeHolder.value().getComponentKeysByTypes(componentTypeSerializerHolders).stream().map(this::getComponentPair).filter(Optional::isPresent).map(Optional::get).map(pair -> pair.mapSecond(component -> (T) component)).toList();
    }

    public <T extends IHotpotSoupComponent> List<Pair<ResourceLocation, T>> getComponentPairsByType(Supplier<? extends IHotpotSoupComponentTypeSerializer<? extends T>> componentTypeSerializerHolder) {
        return getComponentPairsByTypes(List.of(componentTypeSerializerHolder));
    }

    public <T extends IHotpotSoupComponent> List<T> getComponentsByTypes(List<Supplier<? extends IHotpotSoupComponentTypeSerializer<? extends T>>> componentTypeSerializerHolders) {
        return getComponentPairsByTypes(componentTypeSerializerHolders).stream().map(Pair::getSecond).toList();
    }

    public <T extends IHotpotSoupComponent> List<T> getComponentsByType(Supplier<? extends IHotpotSoupComponentTypeSerializer<? extends T>> componentTypeSerializerHolder) {
        return getComponentPairsByType(componentTypeSerializerHolder).stream().map(Pair::getSecond).toList();
    }

    public Optional<Pair<ResourceLocation, IHotpotSoupComponent>> getComponentPair(ResourceLocation resourceLocation) {
        return components.get(resourceLocation) == null ? Optional.empty() : Optional.of(Pair.of(resourceLocation, components.get(resourceLocation).value()));
    }

    public boolean hasComponentType(Supplier<? extends IHotpotSoupComponentTypeSerializer<?>> componentTypeSerializerHolder) {
        return soupTypeHolder.value().hasComponentType(componentTypeSerializerHolder);
    }

    public <T> IHotpotResult<T> getResultFromComponents(IHotpotResult<T> defaultResult, BiFunction<IHotpotSoupComponent, IHotpotResult<T>, IHotpotResult<T>> function) {
        return components.values().stream().map(IndexHolder::value).collect(() -> new AtomicReference<>(defaultResult), (reference, component) -> reference.set(reference.get().isBlocked() ? reference.get() : function.apply(component, reference.get())), (reference1, reference2) -> reference1.set(reference2.get())).get();
    }

    public <T> IHotpotResult<T> getResultFromComponents(BiFunction<IHotpotSoupComponent, IHotpotResult<T>, IHotpotResult<T>> function) {
        return getResultFromComponents(IHotpotResult.pass(), function);
    }

    public LinkedHashMap<ResourceLocation, IndexHolder<IHotpotSoupComponent>> getPartialComponents() {
        return components.sequencedEntrySet().stream().filter(EntryStreams.filterEntryValue(holder -> holder.value().shouldSendToClient())).collect(EntryStreams.ofSequenced());
    }

    public IHotpotResult<Holder<IHotpotContentSerializer<?>>> getPlayerInteractionResult(int position, Player player, InteractionHand hand, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return getResultFromComponents((component, result) -> component.getPlayerInteractionResult(position, player, hand, itemStack, this, pos, result, hotpotBlockEntity));
    }

    public IHotpotResult<Holder<IHotpotContentSerializer<?>>> getContentSerializerResultFromItemStack(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return getResultFromComponents((component, result) -> component.getContentSerializerResultFromItemStack(itemStack, hotpotBlockEntity, this, pos, result));
    }

    public IHotpotResult<IHotpotContent> getContentResultByTableware(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return getResultFromComponents(IHotpotResult.success(content), (component, result) -> component.getContentResultByTableware(hotpotBlockEntity, this, pos, result));
    }

    public IHotpotResult<IHotpotContent> getContentResultByHand(IHotpotResult<IHotpotContent> content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return getResultFromComponents(content, (component, result) -> component.getContentResultByHand(hotpotBlockEntity, this, pos, result));
    }

    public double getContentTickSpeed(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return getResultFromComponents(IHotpotResult.success(0.0), (component, result) -> component.getContentTickSpeed(hotpotBlockEntity, this, pos, result)).orElse(0.0);
    }

    public List<IHotpotSoupComponentSynchronizer> getSoupComponentSynchronizers(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return components.values().stream().map(IndexHolder::value).map(component -> component.getSoupComponentSynchronizer(hotpotBlockEntity, this, pos)).filter(Optional::isPresent).map(Optional::get).toList();
    }

    public boolean isHotpotLit(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return getResultFromComponents(IHotpotResult.success(true), (component, result) -> component.getHotpotLit(hotpotBlockEntity, this, pos, result)).orElse(true);
    }

    public double getWaterLevel() {
        return getResultFromComponents(IHotpotResult.pass(), IHotpotSoupComponent::getWaterLevel).orElse(0.0);
    }

    public double getOverflowWaterLevel() {
        return getResultFromComponents(IHotpotResult.pass(), IHotpotSoupComponent::getOverflowWaterLevel).orElse(0.0);
    }

    public void onAwardExperience(double experience, HotpotBlockEntity blockEntity, LevelBlockPos pos) {
        getResultFromComponents(IHotpotResult.success(experience), (component, result) -> component.onAwardExperience(blockEntity, this, pos, result));
    }

    public void onContentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        getResultFromComponents(IHotpotResult.success(content), (component, result) -> component.onContentUpdate(hotpotBlockEntity, this, pos, result));
    }

    public void onDiscardOverflowWaterLevel(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        components.values().stream().map(IndexHolder::value).forEach(component -> component.onDiscardOverflowWaterLevel(hotpotBlockEntity, this, pos));
    }

    public  void onEntityInside(Entity entity, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        components.values().stream().map(IndexHolder::value).forEach(component -> component.onEntityInside(entity, hotpotBlockEntity, this, pos));
    }

    public  void onTick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        components.values().stream().map(IndexHolder::value).forEach(component -> component.onTick(hotpotBlockEntity, this, pos));
    }

    public void setWaterLevelWithOverflow(double waterLevel, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        components.values().stream().map(IndexHolder::value).forEach(component -> component.setWaterLevelWithOverflow(waterLevel, hotpotBlockEntity, this, pos));
    }

    public void setWaterLevel(double waterLevel, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        components.values().stream().map(IndexHolder::value).forEach(component -> component.setWaterLevel(waterLevel, hotpotBlockEntity, this, pos));
    }
}
