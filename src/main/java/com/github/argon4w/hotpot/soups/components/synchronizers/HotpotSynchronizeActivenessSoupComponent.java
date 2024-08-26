package com.github.argon4w.hotpot.soups.components.synchronizers;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.components.AbstractHotpotSoupComponent;
import com.github.argon4w.hotpot.soups.components.HotpotSoupComponentTypeSerializers;
import com.github.argon4w.hotpot.soups.components.containers.HotpotActivenessContainerSoupComponent;

import java.util.Optional;

public class HotpotSynchronizeActivenessSoupComponent extends AbstractHotpotSoupComponent {
    @Override
    public Optional<IHotpotSoupComponentSynchronizer> getSoupComponentSynchronizer(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos) {
        return Optional.of(new Synchronizer());
    }

    public static class Synchronizer implements IHotpotSoupComponentSynchronizer {
        private double totalActiveness;

        @Override
        public void collect(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos) {
            totalActiveness += soup.getComponentsByType(HotpotSoupComponentTypeSerializers.ACTIVENESS_CONTAINER_SOUP_COMPONENT_TYPE_SERIALIZER).stream().mapToDouble(HotpotActivenessContainerSoupComponent::getActiveness).average().orElse(0.0);
        }

        @Override
        public void apply(int size, HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos) {
            soup.getComponentsByType(HotpotSoupComponentTypeSerializers.ACTIVENESS_CONTAINER_SOUP_COMPONENT_TYPE_SERIALIZER).forEach(component -> component.setActiveness(totalActiveness / size));
        }
    }
}
