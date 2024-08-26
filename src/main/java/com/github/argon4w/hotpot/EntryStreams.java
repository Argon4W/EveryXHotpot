package com.github.argon4w.hotpot;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntryStreams {
    public static <K, V> Collector<Map.Entry<K, V>, ?, LinkedHashMap<K, V>> ofSequenced() {
        return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new);
    }

    public static <K, V> Collector<Map.Entry<K, V>, ?, Map<K, V>> of() {
        return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    public static  <K, V1, V2> Function<Map.Entry<K, V1>, Map.Entry<K, V2>> mapEntryValue(Function<V1, V2> function) {
        return entry -> Map.entry(entry.getKey(), function.apply(entry.getValue()));
    }

    public static  <K, V1, V2> Function<Map.Entry<K, V1>, Map.Entry<K, V2>> mapEntryValue(BiFunction<K, V1, V2> function) {
        return entry -> Map.entry(entry.getKey(), function.apply(entry.getKey(), entry.getValue()));
    }

    public static <K, V> Predicate<Map.Entry<K, V>> filterEntryValue(Predicate<V> predicate) {
        return p -> predicate.test(p.getValue());
    }
}
