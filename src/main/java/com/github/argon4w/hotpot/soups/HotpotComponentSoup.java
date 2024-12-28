package com.github.argon4w.hotpot.soups;

import com.github.argon4w.fancytoys.blocks.LevelBlockPos;
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

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public final class HotpotComponentSoup {

    private final SequencedMap<ResourceLocation, Sorted<IHotpotSoupComponent>> components;
    private final SequencedMap<ResourceLocation, IHotpotSoupComponent> componentValues;
    private final Holder<HotpotComponentSoupType> soupTypeHolder;

    public HotpotComponentSoup(SequencedMap<ResourceLocation, Sorted<IHotpotSoupComponent>> components, Holder<HotpotComponentSoupType> soupTypeHolder) {
        this.components = components;
        this.soupTypeHolder = soupTypeHolder;

        this.componentValues = new LinkedHashMap<>();
        this.components.forEach((key, sorted) -> componentValues.put(key, sorted.value()));
    }

    @SuppressWarnings("unchecked")
    public <T extends IHotpotSoupComponent> List<Pair<ResourceLocation, T>> getComponentPairsByTypes(List<Supplier<? extends IHotpotSoupComponentTypeSerializer<? extends T>>> componentTypeSerializerHolders) {
        List<ResourceLocation> keys = soupTypeHolder.value().getComponentKeysByTypes(componentTypeSerializerHolders);
        List<Pair<ResourceLocation, T>> componentPairs = new ArrayList<>(keys.size());

        for (ResourceLocation key : keys) {
            getComponentPair(key).ifPresent(pair -> componentPairs.add(pair.mapSecond(component -> (T) component)));
        }

        return componentPairs;
    }

    public <T> IHotpotResult<T> getResultFromComponents(IHotpotResult<T> defaultResult, BiFunction<IHotpotSoupComponent, IHotpotResult<T>, IHotpotResult<T>> function) {
        IHotpotResult<T> result = defaultResult;

        for (ResourceLocation key : components.sequencedKeySet()) {
            result = result.isBlocked() ? result : function.apply(componentValues.get(key), result);
        }

        return result;
    }

    public SequencedMap<ResourceLocation, Sorted<IHotpotSoupComponent>> getPartialComponents() {
        LinkedHashMap<ResourceLocation, Sorted<IHotpotSoupComponent>> partialComponents = new LinkedHashMap<>();

        for (ResourceLocation key : components.sequencedKeySet()) {
            Sorted<IHotpotSoupComponent> sorted = components.get(key);

            if (sorted.value().shouldSendToClient()) {
                partialComponents.put(key, sorted);
            }
        }

        return partialComponents;
    }

    public <T extends IHotpotSoupComponent> List<T> getComponentsByTypes(List<Supplier<? extends IHotpotSoupComponentTypeSerializer<? extends T>>> componentTypeSerializerHolders) {
        List<Pair<ResourceLocation, T>> pairs = getComponentPairsByTypes(componentTypeSerializerHolders);
        List<T> components = new ArrayList<>(pairs.size());

        for (Pair<ResourceLocation, T> pair : pairs) {
            components.add(pair.getSecond());
        }

        return components;
    }

    public <T extends IHotpotSoupComponent> List<T> getComponentsByType(Supplier<? extends IHotpotSoupComponentTypeSerializer<? extends T>> componentTypeSerializerHolder) {
        List<Pair<ResourceLocation, T>> pairs = getComponentPairsByTypes(List.of(componentTypeSerializerHolder));
        List<T> components = new ArrayList<>(pairs.size());

        for (Pair<ResourceLocation, T> pair : pairs) {
            components.add(pair.getSecond());
        }

        return components;
    }

    public Optional<Pair<ResourceLocation, IHotpotSoupComponent>> getComponentPair(ResourceLocation resourceLocation) {
        return componentValues.get(resourceLocation) == null
                ? Optional.empty()
                : Optional.of(Pair.of(resourceLocation, componentValues.get(resourceLocation)));
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
        List<IHotpotSoupSyncData> syncDataList = new ArrayList<>();

        for (IHotpotSoupComponent component : componentValues.values()) {
            component.getSoupComponenentSyncData(hotpotBlockEntity, this, pos).ifPresent(syncDataList::add);
        }

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

    public SequencedMap<ResourceLocation, Sorted<IHotpotSoupComponent>> components() {
        return components;
    }

    public SequencedMap<ResourceLocation, IHotpotSoupComponent> componentValues() {
        return componentValues;
    }

    public Holder<HotpotComponentSoupType> soupTypeHolder() {
        return soupTypeHolder;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        var that = (HotpotComponentSoup) obj;

        return Objects.equals(this.components, that.components) &&
                Objects.equals(this.soupTypeHolder, that.soupTypeHolder) &&
                Objects.equals(this.componentValues, that.componentValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(components, soupTypeHolder);
    }

    @Override
    public String toString() {
        return "HotpotComponentSoup[" +
                "components=" + components + ", " +
                "soupTypeHolder=" + soupTypeHolder + ']';
    }
}
