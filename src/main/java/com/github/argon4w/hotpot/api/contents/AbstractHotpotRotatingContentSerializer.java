package com.github.argon4w.hotpot.api.contents;

import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import org.joml.Math;

public abstract class AbstractHotpotRotatingContentSerializer<T extends IHotpotContent> implements IHotpotContentSerializer<T> {
    @Override
    public int indexToPosition(int index, int time) {
        return (index + (int) Math.floor((((time / 20.0 / 60.0) * 360.0 + HotpotBlockEntity.ROTATING_CONTENT_INTERVAL / 2.0) % 360.0) / HotpotBlockEntity.ROTATING_CONTENT_INTERVAL)) % 8;
    }

    @Override
    public int positionToIndex(int clickPosition, int time) {
        return ((clickPosition - (int) Math.floor((((time / 20.0 / 60.0) * 360.0 + HotpotBlockEntity.ROTATING_CONTENT_INTERVAL / 2.0) % 360.0) / HotpotBlockEntity.ROTATING_CONTENT_INTERVAL)) + 8) % 8;
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
