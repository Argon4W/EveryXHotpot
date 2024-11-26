package com.github.argon4w.fancytoys.functions;

import java.util.function.*;

public class Curry {

    public static <P1, P2> Predicate<P2> of(BiPredicate<P1, P2> predicate, P1 p1) {
        return p2 -> predicate.test(p1, p2);
    }

    public static <P1, P2> Consumer<P2> of(BiConsumer<P1, P2> consumer, P1 p1) {
        return p2 -> consumer.accept(p1, p2);
    }

    public static <P1, P2, R> Function<P2, R> of(BiFunction<P1, P2, R> function, P1 p1) {
        return p2 -> function.apply(p1, p2);
    }
}
