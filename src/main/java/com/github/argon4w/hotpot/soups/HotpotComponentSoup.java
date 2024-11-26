package com.github.argon4w.hotpot.soups;

import com.github.argon4w.fancytoys.blocks.LevelBlockPos;
import com.github.argon4w.fancytoys.streams.EntryStream;
import com.github.argon4w.fancytoys.codecs.Sorted;
import com.github.argon4w.hotpot.api.IHotpotResult;
import com.github.argon4w.hotpot.api.contents.IHotpotContent;
import com.github.argon4w.hotpot.api.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.api.items.IHotpotTablewareInteraction;
import com.github.argon4w.hotpot.api.soups.components.IHotpotSoupComponent;
import com.github.argon4w.hotpot.api.soups.components.IHotpotSoupComponentTypeSerializer;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.components.synchronizers.IHotpotSoupSyncData;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Optional;
import java.util.SequencedMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public record HotpotComponentSoup(SequencedMap<ResourceLocation, Sorted<IHotpotSoupComponent>> components, Holder<HotpotComponentSoupType> soupTypeHolder) {

    @SuppressWarnings("unchecked")
    public <T extends IHotpotSoupComponent> List<Pair<ResourceLocation, T>> getComponentPairsByTypes(List<Supplier<? extends IHotpotSoupComponentTypeSerializer<? extends T>>> componentTypeSerializerHolders) {
        return soupTypeHolder
                .value()
                .getComponentKeysByTypes(componentTypeSerializerHolders)
                .stream()
                .map(this::getComponentPair)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(pair -> pair.mapSecond(component -> (T) component))
                .toList();
    }

    public <T> IHotpotResult<T> getResultFromComponents(IHotpotResult<T> defaultResult, BiFunction<IHotpotSoupComponent, IHotpotResult<T>, IHotpotResult<T>> function) {
        return EntryStream
                .fromSequencedMap(components)
                .mapValue(Sorted::value)
                .collectValue(() -> new AtomicReference<>(defaultResult), (reference, component) -> reference.set(reference.get().isBlocked() ? reference.get() : function.apply(component, reference.get())))
                .get();
    }

    public SequencedMap<ResourceLocation, Sorted<IHotpotSoupComponent>> getPartialComponents() {
        return EntryStream
                .fromSequencedMap(components)
                .filterValue(holder -> holder.value().shouldSendToClient())
                .toSequencedMap();
    }

    public <T extends IHotpotSoupComponent> List<T> getComponentsByTypes(List<Supplier<? extends IHotpotSoupComponentTypeSerializer<? extends T>>> componentTypeSerializerHolders) {
        return getComponentPairsByTypes(componentTypeSerializerHolders).stream()
                .map(Pair::getSecond)
                .toList();
    }

    public <T extends IHotpotSoupComponent> List<T> getComponentsByType(Supplier<? extends IHotpotSoupComponentTypeSerializer<? extends T>> componentTypeSerializerHolder) {
        return getComponentPairsByTypes(List.of(componentTypeSerializerHolder))
                .stream()
                .map(Pair::getSecond)
                .toList();
    }

    public Optional<Pair<ResourceLocation, IHotpotSoupComponent>> getComponentPair(ResourceLocation resourceLocation) {
        return components.get(resourceLocation) == null
                ? Optional.empty()
                : Optional.of(Pair.of(resourceLocation, components.get(resourceLocation).value()));
    }

    public boolean hasComponentType(Supplier<? extends IHotpotSoupComponentTypeSerializer<?>> componentTypeSerializerHolder) {
        return soupTypeHolder
                .value()
                .hasComponentType(componentTypeSerializerHolder);
    }

    public IHotpotResult<Holder<IHotpotContentSerializer<?>>> getPlayerInteractionResult(IHotpotTablewareInteraction.Context context, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity) {
        return getResultFromComponents(
                IHotpotResult.pass(),
                (component, result) -> component.getPlayerInteractionResult(context, itemStack, result, hotpotBlockEntity, this));
    }

    public IHotpotResult<Holder<IHotpotContentSerializer<?>>> getContentSerializerResultFromItemStack(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return getResultFromComponents(
                IHotpotResult.pass(),
                (component, result) -> component.getContentSerializerResultFromItemStack(itemStack, hotpotBlockEntity, this, pos, result));
    }

    public IHotpotResult<IHotpotContent> getContentResultByTableware(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return getResultFromComponents(
                IHotpotResult.success(content),
                (component, result) -> component.getContentResultByTableware(hotpotBlockEntity, this, pos, result));
    }

    public IHotpotResult<IHotpotContent> getContentResultByHand(IHotpotResult<IHotpotContent> content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return getResultFromComponents(
                content,
                (component, result) -> component.getContentResultByHand(hotpotBlockEntity, this, pos, result));
    }

    public double getContentTickSpeed(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return getResultFromComponents(
                IHotpotResult.success(0.0),
                (component, result) -> component.getContentTickSpeed(hotpotBlockEntity, this, pos, result)).orElse(0.0);
    }

    public boolean isHotpotLit(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return getResultFromComponents(
                IHotpotResult.success(true),
                (component, result) -> component.getHotpotLit(hotpotBlockEntity, this, pos, result)).orElse(true);
    }

    public double getWaterLevel() {
        return getResultFromComponents(
                IHotpotResult.pass(),
                IHotpotSoupComponent::getWaterLevel).orElse(0.0);
    }

    public double getOverflowWaterLevel() {
        return getResultFromComponents(
                IHotpotResult.pass(),
                IHotpotSoupComponent::getOverflowWaterLevel).orElse(0.0);
    }

    public void onAwardExperience(double experience, HotpotBlockEntity blockEntity, LevelBlockPos pos) {
        getResultFromComponents(
                IHotpotResult.success(experience),
                (component, result) -> component.onAwardExperience(blockEntity, this, pos, result));
    }

    public void onContentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        getResultFromComponents(
                IHotpotResult.success(content),
                (component, result) -> component.onContentUpdate(hotpotBlockEntity, this, pos, result));
    }

    public List<IHotpotSoupSyncData> getSyncData(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return components
                .values()
                .stream()
                .map(Sorted::value)
                .map(component -> component.getSoupComponenentSyncData(hotpotBlockEntity, this, pos))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    public void onDiscardOverflowWaterLevel(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        components
                .values()
                .stream()
                .map(Sorted::value)
                .forEach(component -> component.onDiscardOverflowWaterLevel(hotpotBlockEntity, this, pos));
    }

    public void onEntityInside(Entity entity, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        components
                .values()
                .stream()
                .map(Sorted::value)
                .forEach(component -> component.onEntityInside(entity, hotpotBlockEntity, this, pos));
    }

    public void onTick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        components
                .values()
                .stream()
                .map(Sorted::value)
                .forEach(component -> component.onTick(hotpotBlockEntity, this, pos));
    }

    public void setWaterLevelWithOverflow(double waterLevel, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        components
                .values().stream()
                .map(Sorted::value)
                .forEach(component -> component.setWaterLevelWithOverflow(waterLevel, hotpotBlockEntity, this, pos));
    }

    public void setWaterLevel(double waterLevel, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        components
                .values()
                .stream()
                .map(Sorted::value)
                .forEach(component -> component.setWaterLevel(waterLevel, hotpotBlockEntity, this, pos));
    }
}
