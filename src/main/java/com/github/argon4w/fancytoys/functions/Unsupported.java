package com.github.argon4w.fancytoys.functions;

import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;

public final class Unsupported {

    private Unsupported() {

    }

    public static <T> BinaryOperator<T> combinerOperator() {
        return Unsupported::throwUnsupported;
    }

    public static <T> BiConsumer<T, T> combinerConsumer() {
        return Unsupported::throwUnsupported;
    }

    public static <T> T throwUnsupported(T t1, T t2) {
        throw new UnsupportedOperationException("Use parallel equivalent instead");
    }
}
