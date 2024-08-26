package com.github.argon4w.hotpot.blocks;

import java.util.Iterator;

public class SizedIterator<T> implements Iterator<T> {
    private final Iterator<T> iterator;
    private final int size;
    private int count;

    public SizedIterator(Iterator<T> iterator, int size) {
        this.iterator = iterator;
        this.size = size;
        this.count = 0;
    }

    @Override
    public boolean hasNext() {
        return count < size && iterator.hasNext();
    }

    @Override
    public T next() {
        count ++;
        return iterator.next();
    }
}
