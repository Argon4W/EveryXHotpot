package com.github.argon4w.fancytoys.streams;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.argon4w.fancytoys.functions.Unsupported;
import org.apache.commons.lang3.stream.Streams;

public class EntryStream<K, V> {
    protected final Stream<Map.Entry<K, V>> stream;

    public EntryStream(Stream<Map.Entry<K, V>> stream) {
        this.stream = stream;
    }

    public EntryStream<V, K> swap() {
        return new EntryStream<>(stream.map(entry -> Map.entry(entry.getValue(), entry.getKey())));
    }

    public long count() {
        return stream.count();
    }

    public Stream<K> keys() {
        return stream.map(Map.Entry::getKey);
    }

    public Stream<V> values() {
        return stream.map(Map.Entry::getValue);
    }

    public EntryStream<K, V> nonNullKey() {
        return filterKey(Objects::nonNull);
    }

    public EntryStream<K, V> nonNullValue() {
        return filterValue(Objects::nonNull);
    }

    public EntryStream<K, V> sequential() {
        return new EntryStream<>(stream.sequential());
    }

    public <K2> EntryStream<K2, V> ensureKey(Class<K2> clazz) {
        return filterKey(clazz::isInstance).mapKey(clazz::cast);
    }

    public <V2> EntryStream<K, V2> ensureValue(Class<V2> clazz) {
        return filterValue(clazz::isInstance).mapValue(clazz::cast);
    }

    public EntryStream<K, V> filter(BiPredicate<K, V> filter) {
        return new EntryStream<>(stream.filter(entry -> filter.test(entry.getKey(), entry.getValue())));
    }

    public EntryStream<K, V> filterKey(Predicate<K> filter) {
        return new EntryStream<>(stream.filter(entry -> filter.test(entry.getKey())));
    }

    public EntryStream<K, V> filterValue(Predicate<V> filter) {
        return new EntryStream<>(stream.filter(entry -> filter.test(entry.getValue())));
    }

    public <K2, V2> EntryStream<K2, V2> map(Function<K, K2> keyMapper, Function<V, V2> valueMapper) {
        return new EntryStream<>(stream.map(entry -> Map.entry(keyMapper.apply(entry.getKey()), valueMapper.apply(entry.getValue()))));
    }

    public <K2, V2> EntryStream<K2, V2> map(BiFunction<K, V, K2> keyMapper, BiFunction<K, V, V2> valueMapper) {
        return new EntryStream<>(stream.map(entry -> Map.entry(keyMapper.apply(entry.getKey(), entry.getValue()), valueMapper.apply(entry.getKey(), entry.getValue()))));
    }

    public <K2, V2> OptionalEntryStream<K2, V2> mapOptional(BiFunction<K, V, K2> keyMapper, BiFunction<K, V, Optional<V2>> valueMapper) {
        return new OptionalEntryStream<>(map(keyMapper, valueMapper));
    }

    public <K2, V2> OptionalEntryStream<K2, V2> mapOptional(Function<K, K2> keyMapper, Function<V, Optional<V2>> valueMapper) {
        return new OptionalEntryStream<>(map(keyMapper, valueMapper));
    }

    public <K2> EntryStream<K2, V> mapKey(Function<K, K2> keyMapper) {
        return new EntryStream<>(stream.map(entry -> Map.entry(keyMapper.apply(entry.getKey()), entry.getValue())));
    }

    public <K2> EntryStream<K2, V> mapKey(BiFunction<K, V, K2> keyMapper) {
        return new EntryStream<>(stream.map(entry -> Map.entry(keyMapper.apply(entry.getKey(), entry.getValue()), entry.getValue())));
    }

    public <V2> EntryStream<K, V2> mapValue(Function<V, V2> valueMapper) {
        return new EntryStream<>(stream.map(entry -> Map.entry(entry.getKey(), valueMapper.apply(entry.getValue()))));
    }

    public <V2> EntryStream<K, V2> mapValue(BiFunction<K, V, V2> valueMapper) {
        return new EntryStream<>(
                stream.map(entry -> Map.entry(entry.getKey(), valueMapper.apply(entry.getKey(), entry.getValue()))));
    }

    public <V2> OptionalEntryStream<K, V2> mapValueOptional(Function<V, Optional<V2>> valueMapper) {
        return new OptionalEntryStream<>(mapValue(valueMapper));
    }

    public <V2> OptionalEntryStream<K, V2> mapValueOptional(BiFunction<K, V, Optional<V2>> valueMapper) {
        return new OptionalEntryStream<>(mapValue(valueMapper));
    }

    public <K2, V2> EntryStream<K2, V2> flatMap(BiFunction<K, V, EntryStream<K2, V2>> mapper) {
        return new EntryStream<>(stream.flatMap(entry -> mapper.apply(entry.getKey(), entry.getValue()).stream));
    }

    public <K2, V2> OptionalEntryStream<K2, V2> flatMapOptional(BiFunction<K, V, EntryStream<K2, Optional<V2>>> mapper) {
        return new OptionalEntryStream<>(flatMap(mapper));
    }

    public <K2, V2> OptionalEntryStream<K2, V2> flatMapKeyOptional(Function<K, EntryStream<K2, Optional<V2>>> mapper) {
        return new OptionalEntryStream<>(flatMapKey(mapper));
    }

    public <K2, V2> EntryStream<K2, V2> flatMapKey(Function<K, EntryStream<K2, V2>> mapper) {
        return new EntryStream<>(stream.flatMap(entry -> mapper.apply(entry.getKey()).stream));
    }

    public <K2, V2> EntryStream<K2, V2> flatMapValue(Function<V, EntryStream<K2, V2>> mapper) {
        return new EntryStream<>(stream.flatMap(entry -> mapper.apply(entry.getValue()).stream));
    }

    public <K2, V2> OptionalEntryStream<K2, V2> flatMapValueOptional(Function<V, EntryStream<K2, Optional<V2>>> mapper) {
        return new OptionalEntryStream<>(flatMapValue(mapper));
    }

    public <K2, V2> EntryStream<K2, V2> mapMulti(EntryMultiMapper<K, V, K2, V2> mapper) {
        return new EntryStream<>(stream.mapMulti((entry, buffer) -> mapper.mapMulti(entry.getKey(), entry.getValue(), (k2, v2) -> buffer.accept(Map.entry(k2, v2)))));
    }

    public <K2, V2> OptionalEntryStream<K2, V2> mapMultiOptional(EntryMultiMapper<K, V, K2, Optional<V2>> mapper) {
        return new OptionalEntryStream<>(mapMulti(mapper));
    }

    public <K2> EntryStream<K2, V> mapMultiKey(BiConsumer<K, Consumer<K2>> mapper) {
        return new EntryStream<>(stream.mapMulti((entry, buffer) -> mapper.accept(entry.getKey(), k2 -> buffer.accept(Map.entry(k2, entry.getValue())))));
    }

    public <V2> EntryStream<K, V2> mapMultiValue(BiConsumer<V, Consumer<V2>> mapper) {
        return new EntryStream<>(stream.mapMulti((entry, buffer) -> mapper.accept(entry.getValue(), v2 -> buffer.accept(Map.entry(entry.getKey(), v2)))));
    }

    public <V2> OptionalEntryStream<K, V2> mapMultiValueOptional(BiConsumer<V, Consumer<Optional<V2>>> mapper) {
        return new OptionalEntryStream<>(mapMultiValue(mapper));
    }

    public EntryStream<K, V> sortedKey(Comparator<? super K> comparator) {
        return new EntryStream<>(stream.sorted((entry1, entry2) -> comparator.compare(entry1.getKey(), entry2.getKey())));
    }

    public EntryStream<K, V> sortedValue(Comparator<? super V> comparator) {
        return new EntryStream<>(stream.sorted((entry1, entry2) -> comparator.compare(entry1.getValue(), entry2.getValue())));
    }

    public EntryStream<K, V> peek(BiConsumer<K, V> action) {
        return new EntryStream<>(stream.peek(entry -> action.accept(entry.getKey(), entry.getValue())));
    }

    public EntryStream<K, V> peekKey(Consumer<K> action) {
        return new EntryStream<>(stream.peek(entry -> action.accept(entry.getKey())));
    }

    public EntryStream<K, V> peekValue(Consumer<V> action) {
        return new EntryStream<>(stream.peek(entry -> action.accept(entry.getValue())));
    }

    public EntryStream<K, V> limit(long maxSize) {
        return new EntryStream<>(stream.limit(maxSize));
    }

    public EntryStream<K, V> skip(long n) {
        return new EntryStream<>(stream.skip(n));
    }

    public EntryStream<K, V> takeWhile(BiPredicate<K, V> filter) {
        return new EntryStream<>(stream.takeWhile(entry -> filter.test(entry.getKey(), entry.getValue())));
    }

    public EntryStream<K, V> takeWhileKey(Predicate<K> filter) {
        return new EntryStream<>(stream.takeWhile(entry -> filter.test(entry.getKey())));
    }

    public EntryStream<K, V> takeWhileValue(Predicate<V> filter) {
        return new EntryStream<>(stream.takeWhile(entry -> filter.test(entry.getValue())));
    }

    public EntryStream<K, V> dropWhile(BiPredicate<K, V> predicate) {
        return new EntryStream<>(stream.dropWhile(entry -> predicate.test(entry.getKey(), entry.getValue())));
    }

    public EntryStream<K, V> dropWhileKey(Predicate<K> predicate) {
        return new EntryStream<>(stream.dropWhile(entry -> predicate.test(entry.getKey())));
    }

    public EntryStream<K, V> dropWhileValue(Predicate<V> predicate) {
        return new EntryStream<>(stream.dropWhile(entry -> predicate.test(entry.getValue())));
    }

    public void forEach(BiConsumer<K, V> action) {
        stream.forEach(entry -> action.accept(entry.getKey(), entry.getValue()));
    }

    public void forEachKey(Consumer<K> action) {
        stream.forEach(entry -> action.accept(entry.getKey()));
    }

    public void forEachValue(Consumer<V> action) {
        stream.forEach(entry -> action.accept(entry.getValue()));
    }

    public void forEachOrdered(BiConsumer<K, V> action) {
        stream.forEachOrdered(entry -> action.accept(entry.getKey(), entry.getValue()));
    }

    public void forEachKeyOrdered(Consumer<K> action) {
        stream.forEachOrdered(entry -> action.accept(entry.getKey()));
    }

    public void forEachValueOrdered(Consumer<V> action) {
        stream.forEachOrdered(entry -> action.accept(entry.getValue()));
    }

    public K reduceKey(K identity, BinaryOperator<K> accumulator) {
        return keys().reduce(identity, accumulator);
    }

    public V reduceValue(V identity, BinaryOperator<V> accumulator) {
        return values().reduce(identity, accumulator);
    }

    public Optional<K> reduceKey(BinaryOperator<K> accumulator) {
        return keys().reduce(accumulator);
    }

    public Optional<V> reduceValue(BinaryOperator<V> accumulator) {
        return values().reduce(accumulator);
    }

    public <R> R reduce(R identity, EntryReducer<R, K, V> reducer) {
        return stream.sequential().reduce(identity, (r, entry) -> reducer.reduce(r, entry.getKey(), entry.getValue()), Unsupported.combinerOperator());
    }

    public <R> R reduceKey(R identity, BiFunction<R, K, R> reducer) {
        return stream.sequential().reduce(identity, (r, entry) -> reducer.apply(r, entry.getKey()), Unsupported.combinerOperator());
    }

    public <R> R reduceValue(R identity, BiFunction<R, V, R> reducer) {
        return stream.sequential().reduce(identity, (r, entry) -> reducer.apply(r, entry.getValue()), Unsupported.combinerOperator());
    }

    public <R> R reduceParallel(R identity, EntryReducer<R, K, V> reducer, BinaryOperator<R> combiner) {
        return stream.reduce(identity, (r, entry) -> reducer.reduce(r, entry.getKey(), entry.getValue()), combiner);
    }

    public <R> R reduceKeyParallel(R identity, BiFunction<R, K, R> reducer, BinaryOperator<R> combiner) {
        return stream.reduce(identity, (r, entry) -> reducer.apply(r, entry.getKey()), combiner);
    }

    public <R> R reduceValueParallel(R identity, BiFunction<R, V, R> reducer, BinaryOperator<R> combiner) {
        return stream.reduce(identity, (r, entry) -> reducer.apply(r, entry.getValue()), combiner);
    }

    public <R> R collect(Supplier<R> supplier, EntryAccumulator<R, K, V> accumulator) {
        return stream.sequential().collect(supplier, (r, entry) -> accumulator.accumulate(r, entry.getKey(), entry.getValue()), Unsupported.combinerConsumer());
    }

    public <R> R collectKey(Supplier<R> supplier, BiConsumer<R, K> accumulator) {
        return stream.sequential().collect(supplier, (r, entry) -> accumulator.accept(r, entry.getKey()), Unsupported.combinerConsumer());
    }

    public <R> R collectValue(Supplier<R> supplier, BiConsumer<R, V> accumulator) {
        return stream.sequential().collect(supplier, (r, entry) -> accumulator.accept(r, entry.getValue()), Unsupported.combinerConsumer());
    }

    public <R> R collectParallel(
            Supplier<R> supplier, EntryAccumulator<R, K, V> accumulator, BiConsumer<R, R> combiner) {
        return stream.collect(supplier, (r, entry) -> accumulator.accumulate(r, entry.getKey(), entry.getValue()), combiner);
    }

    public <R> R collectKeyParallel(Supplier<R> supplier, BiConsumer<R, K> accumulator, BiConsumer<R, R> combiner) {
        return stream.collect(supplier, (r, entry) -> accumulator.accept(r, entry.getKey()), combiner);
    }

    public <R> R collectValueParallel(Supplier<R> supplier, BiConsumer<R, V> accumulator, BiConsumer<R, R> combiner) {
        return stream.collect(supplier, (r, entry) -> accumulator.accept(r, entry.getValue()), combiner);
    }

    public Map<K, V> toMap() {
        return stream.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public <M extends Map<K, V>> M toMap(Supplier<M> supplier) {
        return stream.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2, supplier));
    }

    public SequencedMap<K, V> toSequencedMap() {
        return stream.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2, LinkedHashMap::new));
    }

    public <R> R toMap(Function<Map<K, V>, R> function) {
        return function.apply(toMap());
    }

    public <R> R toSequencedMap(Function<SequencedMap<K, V>, R> function) {
        return function.apply(toSequencedMap());
    }

    public Optional<Map.Entry<K, V>> minByKey(Comparator<K> keyComparator) {
        return stream.min((entry1, entry2) -> keyComparator.compare(entry1.getKey(), entry2.getKey()));
    }

    public Optional<Map.Entry<K, V>> minByValue(Comparator<V> valueComparator) {
        return stream.max((entry1, entry2) -> valueComparator.compare(entry1.getValue(), entry2.getValue()));
    }

    public Optional<K> minKeyByKey(Comparator<K> keyComparator) {
        return minByKey(keyComparator).map(Map.Entry::getKey);
    }

    public Optional<V> minValueByKey(Comparator<K> keyComparator) {
        return minByKey(keyComparator).map(Map.Entry::getValue);
    }

    public Optional<K> minKeyByValue(Comparator<V> valueComparator) {
        return minByValue(valueComparator).map(Map.Entry::getKey);
    }

    public Optional<V> minValueByValue(Comparator<V> valueComparator) {
        return minByValue(valueComparator).map(Map.Entry::getValue);
    }

    public Optional<Map.Entry<K, V>> maxByKey(Comparator<K> keyComparator) {
        return stream.max((entry1, entry2) -> keyComparator.compare(entry1.getKey(), entry2.getKey()));
    }

    public Optional<Map.Entry<K, V>> maxByValue(Comparator<V> valueComparator) {
        return stream.max((entry1, entry2) -> valueComparator.compare(entry1.getValue(), entry2.getValue()));
    }

    public Optional<K> maxKeyByKey(Comparator<K> keyComparator) {
        return maxByKey(keyComparator).map(Map.Entry::getKey);
    }

    public Optional<V> maxValueByKey(Comparator<K> keyComparator) {
        return maxByKey(keyComparator).map(Map.Entry::getValue);
    }

    public Optional<K> maxKeyByValue(Comparator<V> valueComparator) {
        return maxByValue(valueComparator).map(Map.Entry::getKey);
    }

    public Optional<V> maxValueByValue(Comparator<V> valueComparator) {
        return maxByValue(valueComparator).map(Map.Entry::getValue);
    }

    public boolean anyMatch(BiPredicate<K, V> filter) {
        return stream.anyMatch(entry -> filter.test(entry.getKey(), entry.getValue()));
    }

    public boolean anyMatchKey(Predicate<K> filter) {
        return stream.anyMatch(entry -> filter.test(entry.getKey()));
    }

    public boolean anyMatchValue(Predicate<V> filter) {
        return stream.anyMatch(entry -> filter.test(entry.getValue()));
    }

    public boolean allMatch(BiPredicate<K, V> filter) {
        return stream.allMatch(entry -> filter.test(entry.getKey(), entry.getValue()));
    }

    public boolean allMatchKey(Predicate<K> filter) {
        return stream.allMatch(entry -> filter.test(entry.getKey()));
    }

    public boolean allMatchValue(Predicate<V> filter) {
        return stream.allMatch(entry -> filter.test(entry.getValue()));
    }

    public boolean noneMatch(BiPredicate<K, V> filter) {
        return stream.noneMatch(entry -> filter.test(entry.getKey(), entry.getValue()));
    }

    public boolean noneMatchKey(Predicate<K> filter) {
        return stream.noneMatch(entry -> filter.test(entry.getKey()));
    }

    public boolean noneMatchValue(Predicate<V> filter) {
        return stream.anyMatch(entry -> filter.test(entry.getValue()));
    }

    public Optional<Map.Entry<K, V>> findFirst() {
        return stream.findFirst();
    }

    public void findFirst(BiConsumer<K, V> action) {
        stream.findFirst().ifPresent(entry -> action.accept(entry.getKey(), entry.getValue()));
    }

    public <R> Optional<R> mapFirst(BiFunction<K, V, R> mapper) {
        return stream.findFirst().map(entry -> mapper.apply(entry.getKey(), entry.getValue()));
    }

    public Optional<K> findFirstKey() {
        return stream.findFirst().map(Map.Entry::getKey);
    }

    public Optional<V> findFirstValue() {
        return stream.findFirst().map(Map.Entry::getValue);
    }

    public Optional<Map.Entry<K, V>> findAny() {
        return stream.findAny();
    }

    public void findAny(BiConsumer<K, V> action) {
        stream.findAny().ifPresent(entry -> action.accept(entry.getKey(), entry.getValue()));
    }

    public <R> Optional<R> mapAny(BiFunction<K, V, R> mapper) {
        return stream.findAny().map(entry -> mapper.apply(entry.getKey(), entry.getValue()));
    }

    public Optional<K> findAnyKey() {
        return stream.findAny().map(Map.Entry::getKey);
    }

    public Optional<V> findAnyValue() {
        return stream.findAny().map(Map.Entry::getValue);
    }

    public interface EntryMultiMapper<K1, V1, K2, V2> {
        void mapMulti(K1 k1, V1 k2, BiConsumer<K2, V2> buffer);
    }

    public interface EntryAccumulator<R, K, V> {
        void accumulate(R r, K k, V v);
    }

    public interface EntryReducer<R, K, V> {
        R reduce(R r, K k, V v);
    }

    public static <K, V> EntryStream<K, V> empty() {
        return new EntryStream<>(Stream.empty());
    }

    public static <K, V> EntryStream<K, V> concat(EntryStream<K, V> a, EntryStream<K, V> b) {
        return new EntryStream<>(Stream.concat(a.stream, b.stream));
    }

    public static <K, V> EntryStream<K, V> fromMap(Map<K, V> map) {
        return new EntryStream<>(map.entrySet().stream());
    }

    public static <K, V> EntryStream<K, V> fromKeyValue(K k, V v) {
        return new EntryStream<>(Stream.of(Map.entry(k, v)));
    }

    public static <K, V> EntryStream<K, V> fromEntry(Map.Entry<K, V> entry) {
        return new EntryStream<>(Stream.of(entry));
    }

    public static <K, V> EntryStream<K, V> fromSequencedMap(SequencedMap<K, V> map) {
        return new EntryStream<>(map.sequencedEntrySet().stream());
    }

    public static <K, V> EntryStream<K, V> fromKeys(Stream<K> stream, Function<K, V> valueMapper) {
        return new EntryStream<>(stream.map(k -> Map.entry(k, valueMapper.apply(k))));
    }

    public static <K, V> EntryStream<K, V> fromKeys(List<K> list, Function<K, V> valueMapper) {
        return new EntryStream<>(list.stream().map(k -> Map.entry(k, valueMapper.apply(k))));
    }

    public static <K, V> EntryStream<K, V> fromKeys(Iterator<K> iterator, Function<K, V> valueMapper) {
        return new EntryStream<>(Streams.of(iterator).map(k -> Map.entry(k, valueMapper.apply(k))));
    }

    public static <K, V> EntryStream<K, V> fromValues(Stream<V> stream, Function<V, K> keyMapper) {
        return new EntryStream<>(stream.map(v -> Map.entry(keyMapper.apply(v), v)));
    }

    public static <K, V> EntryStream<K, V> fromValues(List<V> list, Function<V, K> keyMapper) {
        return new EntryStream<>(list.stream().map(v -> Map.entry(keyMapper.apply(v), v)));
    }

    public static <K, V> EntryStream<K, V> fromValues(Iterator<V> iterator, Function<V, K> keyMapper) {
        return new EntryStream<>(Streams.of(iterator).map(v -> Map.entry(keyMapper.apply(v), v)));
    }

    public static <R, K, V> EntryStream<K, V> fromStream(
            Stream<R> stream, Function<R, K> keyMapper, Function<R, V> valueMapper) {
        return new EntryStream<>(stream.map(r -> Map.entry(keyMapper.apply(r), valueMapper.apply(r))));
    }

    public static <K, V> EntryStream<K, V> fromList(List<Map.Entry<K, V>> list) {
        return new EntryStream<>(list.stream());
    }

    public static <K, V> List<Map.Entry<K, V>> toList(Map<K, V> map) {
        return List.copyOf(map.entrySet());
    }

    public static <K, V> List<Map.Entry<K, V>> toList(SequencedMap<K, V> map) {
        return List.copyOf(map.entrySet());
    }

    public static <K, V> Map<K, V> toMap(List<Map.Entry<K, V>> map) {
        return fromList(map).toMap();
    }

    public static <K, V> SequencedMap<K, V> toSequencedMap(List<Map.Entry<K, V>> map) {
        return fromList(map).toSequencedMap();
    }
}
