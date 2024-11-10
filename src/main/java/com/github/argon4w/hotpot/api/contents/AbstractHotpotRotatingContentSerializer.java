package com.github.argon4w.hotpot.api.contents;

import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import org.joml.Math;

public abstract class AbstractHotpotRotatingContentSerializer<T extends IHotpotContent> implements IHotpotContentSerializer<T> {

    @Override
    public int indexToPosition(int index, int time) {
        return (index + getOffset(time)) % 8;
    }

    @Override
    public int positionToIndex(int clickPosition, int time) {
        return ((clickPosition - getOffset(time)) + 8) % 8;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    public int getOffset(int time) {
        double minutes = (time / 20.0 / 60.0);
        double degrees = minutes * 360.0 + HotpotBlockEntity.ROTATING_CONTENT_INTERVAL / 2.0;
        double clampedDegrees = degrees % 360.0;

        return (int) Math.floor(clampedDegrees / HotpotBlockEntity.ROTATING_CONTENT_INTERVAL);
    }
}
