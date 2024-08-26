package com.github.argon4w.hotpot.soups.components.synchronizers;

import com.github.argon4w.hotpot.HotpotMobEffectMap;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.components.AbstractHotpotSoupComponent;
import com.github.argon4w.hotpot.soups.components.HotpotSoupComponentTypeSerializers;
import com.github.argon4w.hotpot.soups.components.containers.HotpotDynamicMobEffectContainerSoupComponent;

import java.util.Optional;

public class HotpotSynchronizeDynamicMobEffectsSoupComponent extends AbstractHotpotSoupComponent {
    @Override
    public Optional<IHotpotSoupComponentSynchronizer> getSoupComponentSynchronizer(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos) {
        return Optional.of(new Synchronizer());
    }

    public static class Synchronizer implements IHotpotSoupComponentSynchronizer {
        private final HotpotMobEffectMap mobEffectMap;

        public Synchronizer() {
            this.mobEffectMap = new HotpotMobEffectMap();
        }

        @Override
        public void collect(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos) {
            soup.getComponentsByType(HotpotSoupComponentTypeSerializers.DYNAMIC_MOB_EFFECT_CONTAINER_SOUP_COMPONENT_TYPE_SERIALIZER).stream().map(HotpotDynamicMobEffectContainerSoupComponent::getMobEffectMap).forEach(mobEffectMap::putEffects);
        }

        @Override
        public void apply(int size, HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos) {
            soup.getComponentsByType(HotpotSoupComponentTypeSerializers.DYNAMIC_MOB_EFFECT_CONTAINER_SOUP_COMPONENT_TYPE_SERIALIZER).forEach(component -> component.setMobEffectMap(mobEffectMap));
        }
    }
}
