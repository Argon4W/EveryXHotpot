package com.github.argon4w.hotpot.api.contents;

public abstract class AbstractHotpotFixedContentSerializer<T extends IHotpotContent> implements IHotpotContentSerializer<T> {
    @Override
    public int indexToPosition(int index, int time) {
        return index;
    }

    @Override
    public int positionToIndex(int clickPosition, int time) {
        return clickPosition;
    }

    @Override
    public int getPriority() {
        return 2;
    }
}
