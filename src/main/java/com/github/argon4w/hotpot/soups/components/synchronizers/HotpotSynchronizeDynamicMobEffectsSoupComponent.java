package com.github.argon4w.hotpot.soups.components.synchronizers;

import com.github.argon4w.fancytoys.LevelBlockPos;
import com.github.argon4w.fancytoys.MobEffectMap;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.components.AbstractHotpotSoupComponent;
import com.github.argon4w.hotpot.soups.components.HotpotSoupComponentTypeSerializers;
import com.github.argon4w.hotpot.soups.components.containers.HotpotDynamicMobEffectContainerSoupComponent;
import java.util.Optional;

public class HotpotSynchronizeDynamicMobEffectsSoupComponent extends AbstractHotpotSoupComponent {

    @Override
    public Optional<IHotpotSoupSyncData> getSoupComponenentSyncData(
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos) {
        return Optional.of(new SyncData());
    }

    public static class SyncData implements IHotpotSoupSyncData {

        private final MobEffectMap mobEffectMap;

        public SyncData() {
            this.mobEffectMap = new MobEffectMap();
        }

        @Override
        public void collect(
                HotpotBlockEntity hotpotBlockEntity,
                HotpotComponentSoup soup,
                LevelBlockPos pos) {
            soup
                    .getComponentsByType(HotpotSoupComponentTypeSerializers.DYNAMIC_MOB_EFFECT_CONTAINER_SOUP_COMPONENT_TYPE_SERIALIZER)
                    .stream()
                    .filter(HotpotDynamicMobEffectContainerSoupComponent::isScheduled)
                    .map(HotpotDynamicMobEffectContainerSoupComponent::getScheduledMobEffectMap)
                    .forEach(mobEffectMap::putEffects);
        }

        @Override
        public void apply(int size, HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos) {
            soup
                    .getComponentsByType(HotpotSoupComponentTypeSerializers.DYNAMIC_MOB_EFFECT_CONTAINER_SOUP_COMPONENT_TYPE_SERIALIZER)
                    .stream()
                    .peek(HotpotDynamicMobEffectContainerSoupComponent::clearScheduledEffects)
                    .forEach(component -> component.putEffects(mobEffectMap));
        }

        @Override
        public boolean shouldApply() {
            return !mobEffectMap.isEmpty();
        }
    }
}
