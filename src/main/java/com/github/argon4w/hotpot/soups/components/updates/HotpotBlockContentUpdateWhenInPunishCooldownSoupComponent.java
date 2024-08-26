package com.github.argon4w.hotpot.soups.components.updates;

import com.github.argon4w.hotpot.IHotpotResult;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.components.AbstractHotpotSoupComponent;
import com.github.argon4w.hotpot.soups.components.HotpotSoupComponentTypeSerializers;
import com.github.argon4w.hotpot.soups.components.containers.HotpotPunishCooldownContainerSoupComponent;

public class HotpotBlockContentUpdateWhenInPunishCooldownSoupComponent extends AbstractHotpotSoupComponent {
    @Override
    public IHotpotResult<IHotpotContent> onContentUpdate(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<IHotpotContent> result) {
        return soup.getComponentsByType(HotpotSoupComponentTypeSerializers.PUNISH_COOLDOWN_CONTAINER_SOUP_COMPONENT_TYPE_SERIALIZER).stream().mapToInt(HotpotPunishCooldownContainerSoupComponent::getEmptyWaterPunishCooldown).sum() > 0 ? IHotpotResult.blocked() : result;
    }
}
