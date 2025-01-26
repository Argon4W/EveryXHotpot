package com.github.argon4w.hotpot.soups;

import com.github.argon4w.fancytoys.blocks.LevelBlockPos;
import com.github.argon4w.fancytoys.codecs.Sorted;
import com.github.argon4w.fancytoys.streams.EntryStream;
import com.github.argon4w.hotpot.api.IHotpotResult;
import com.github.argon4w.hotpot.api.contents.IHotpotContent;
import com.github.argon4w.hotpot.api.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.api.items.IHotpotTablewareInteraction;
import com.github.argon4w.hotpot.api.soups.components.IHotpotSoupComponent;
import com.github.argon4w.hotpot.api.soups.components.IHotpotSoupComponentTypeSerializer;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.components.synchronizers.IHotpotSoupSyncData;
import com.mojang.datafixers.util.Pair;

import java.util.*;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public final class HotpotComponentSoup {

    private final SequencedMap<ResourceLocation, Sorted<IHotpotSoupComponent>> components;
    private final SequencedMap<ResourceLocation, Sorted<IHotpotSoupComponent>> partialComponents;
    private final SequencedMap<ResourceLocation, IHotpotSoupComponent> componentValues;
    private final Object2ObjectMap<List<?>, List<? extends IHotpotSoupComponent>> cachedComponentsByTypes;
    private final Object2ObjectMap<List<?>, List<?>> cachedComponentPairsByTypes;
    private final Holder<HotpotComponentSoupType> soupTypeHolder;

    public HotpotComponentSoup(SequencedMap<ResourceLocation, Sorted<IHotpotSoupComponent>> components, Holder<HotpotComponentSoupType> soupTypeHolder) {
        this.components = components;
        this.soupTypeHolder = soupTypeHolder;

        this.componentValues = new LinkedHashMap<>();
        EntryStream.fromMap(this.components).mapValue(Sorted::value).forEach(this.componentValues::put);

        this.partialComponents = new LinkedHashMap<>();
        EntryStream.fromMap(this.components).filterValue(Sorted.valueFilter(IHotpotSoupComponent::shouldSendToClient)).forEach(this.partialComponents::put);

        this.cachedComponentsByTypes = new Object2ObjectOpenHashMap<>();
        this.cachedComponentPairsByTypes = new Object2ObjectOpenHashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <T extends IHotpotSoupComponent> List<Pair<ResourceLocation, T>> getComponentPairsByTypes(List<Supplier<? extends IHotpotSoupComponentTypeSerializer<? extends T>>> componentTypeSerializerHolders) {
        return (List<Pair<ResourceLocation, T>>) cachedComponentPairsByTypes.computeIfAbsent(componentTypeSerializerHolders, list -> soupTypeHolder
                .value()
                .getComponentKeysByTypes(componentTypeSerializerHolders)
                .stream()
                .map(this::getComponentPair)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList());
    }

    @SuppressWarnings("unchecked")
    public <T extends IHotpotSoupComponent> List<T> getComponentsByTypes(List<Supplier<? extends IHotpotSoupComponentTypeSerializer<? extends T>>> componentTypeSerializerHolders) {
        return (List<T>) cachedComponentsByTypes.computeIfAbsent(componentTypeSerializerHolders, list -> soupTypeHolder
                .value()
                .getComponentKeysByTypes(componentTypeSerializerHolders)
                .stream()
                .map(this::getComponent)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(component -> (T) component)
                .toList());
    }

    public <T extends IHotpotSoupComponent> List<T> getComponentsByType(Supplier<? extends IHotpotSoupComponentTypeSerializer<? extends T>> componentTypeSerializerHolder) {
        return getComponentsByTypes(List.of(componentTypeSerializerHolder));
    }

    public Optional<Pair<ResourceLocation, IHotpotSoupComponent>> getComponentPair(ResourceLocation resourceLocation) {
        return componentValues.get(resourceLocation) == null
                ? Optional.empty()
                : Optional.of(Pair.of(resourceLocation, componentValues.get(resourceLocation)));
    }

    public Optional<IHotpotSoupComponent> getComponent(ResourceLocation resourceLocation) {
        return componentValues.get(resourceLocation) == null
                ? Optional.empty()
                : Optional.of(componentValues.get(resourceLocation));
    }

    public boolean hasComponentType(Supplier<? extends IHotpotSoupComponentTypeSerializer<?>> componentTypeSerializerHolder) {
        return soupTypeHolder
                .value()
                .hasComponentType(componentTypeSerializerHolder);
    }

    public IHotpotResult<Holder<IHotpotContentSerializer<?>>> getPlayerInteractionResult(IHotpotTablewareInteraction.Context context, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity) {
        return IHotpotResult.untilBlock(
                componentValues.sequencedValues(),
                IHotpotResult.pass(),
                (component, result) -> component.getPlayerInteractionResult(context, itemStack, result, hotpotBlockEntity, this));
    }

    public IHotpotResult<Holder<IHotpotContentSerializer<?>>> getContentSerializerResultFromItemStack(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return IHotpotResult.untilBlock(
                componentValues.sequencedValues(),
                IHotpotResult.pass(),
                (component, result) -> component.getContentSerializerResultFromItemStack(itemStack, hotpotBlockEntity, this, pos, result));
    }

    public IHotpotResult<IHotpotContent> getContentResultByTableware(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return IHotpotResult.untilBlock(
                componentValues.sequencedValues(),
                IHotpotResult.success(content),
                (component, result) -> component.getContentResultByTableware(hotpotBlockEntity, this, pos, result));
    }

    public IHotpotResult<IHotpotContent> getContentResultByHand(IHotpotResult<IHotpotContent> content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return IHotpotResult.untilBlock(
                componentValues.sequencedValues(),
                content,
                (component, result) -> component.getContentResultByHand(hotpotBlockEntity, this, pos, result));
    }

    public double getContentTickSpeed(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return IHotpotResult.untilBlock(
                componentValues.sequencedValues(),
                IHotpotResult.success(0.0),
                (component, result) -> component.getContentTickSpeed(hotpotBlockEntity, this, pos, result)).orElse(0.0);
    }

    public boolean isHotpotLit(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return IHotpotResult.untilBlock(
                componentValues.sequencedValues(),
                IHotpotResult.success(true),
                (component, result) -> component.getHotpotLit(hotpotBlockEntity, this, pos, result)).orElse(true);
    }

    public double getWaterLevel() {
        return IHotpotResult.untilBlock(
                componentValues.sequencedValues(),
                IHotpotResult.pass(),
                IHotpotSoupComponent::getWaterLevel).orElse(0.0);
    }

    public double getOverflowWaterLevel() {
        return IHotpotResult.untilBlock(
                componentValues.sequencedValues(),
                IHotpotResult.pass(),
                IHotpotSoupComponent::getOverflowWaterLevel).orElse(0.0);
    }

    public void onAwardExperience(double experience, HotpotBlockEntity blockEntity, LevelBlockPos pos) {
        IHotpotResult.untilBlock(
                componentValues.sequencedValues(),
                IHotpotResult.success(experience),
                (component, result) -> component.onAwardExperience(blockEntity, this, pos, result));
    }

    public void onContentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        IHotpotResult.untilBlock(
                componentValues.sequencedValues(),
                IHotpotResult.success(content),
                (component, result) -> component.onContentUpdate(hotpotBlockEntity, this, pos, result));
    }

    public List<IHotpotSoupSyncData> getSyncData(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        List<IHotpotSoupSyncData> syncDataList = new ArrayList<>();
        componentValues.values().forEach(component -> component.getSoupComponenentSyncData(hotpotBlockEntity, this, pos).ifPresent(syncDataList::add));

        return syncDataList;
    }

    public void onDiscardOverflowWaterLevel(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        componentValues.values().forEach(component -> component.onDiscardOverflowWaterLevel(hotpotBlockEntity, this, pos));
    }

    public void onEntityInside(Entity entity, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        componentValues.values().forEach(component -> component.onEntityInside(entity, hotpotBlockEntity, this, pos));
    }

    public void onTick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        componentValues.values().forEach(component -> component.onTick(hotpotBlockEntity, this, pos));
    }

    public void setWaterLevelWithOverflow(double waterLevel, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        componentValues.values().forEach(component -> component.setWaterLevelWithOverflow(waterLevel, hotpotBlockEntity, this, pos));
    }

    public void setWaterLevel(double waterLevel, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        componentValues.values().forEach(component -> component.setWaterLevel(waterLevel, hotpotBlockEntity, this, pos));
    }

    public SequencedMap<ResourceLocation, Sorted<IHotpotSoupComponent>> getComponents() {
        return components;
    }

    public SequencedMap<ResourceLocation, IHotpotSoupComponent> getComponentValues() {
        return componentValues;
    }

    public SequencedMap<ResourceLocation, Sorted<IHotpotSoupComponent>> getPartialComponents() {
        return partialComponents;
    }

    public Holder<HotpotComponentSoupType> soupTypeHolder() {
        return soupTypeHolder;
    }
}
