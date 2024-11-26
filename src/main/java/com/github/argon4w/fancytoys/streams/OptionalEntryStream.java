package com.github.argon4w.fancytoys.streams;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.argon4w.fancytoys.functions.Unsupported;
import org.apache.commons.lang3.function.Consumers;
import org.apache.commons.lang3.stream.Streams;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class OptionalEntryStream<K, V> {

    protected final Stream<Map.Entry<K, Optional<V>>> stream;

    public OptionalEntryStream(Stream<Map.Entry<K, Optional<V>>> stream) {
        this.stream = stream;
    }

    public OptionalEntryStream(EntryStream<K, Optional<V>> entryStream) {
        this(entryStream.stream);
    }

    public long count() {
        return stream.count();
    }

    public Stream<K> keys() {
        return stream.map(Map.Entry::getKey);
    }

    public Stream<Optional<V>> values() {
        return stream.map(Map.Entry::getValue);
    }

    public EntryStream<K, V> get() {
        return new EntryStream<>(stream).mapValue(Optional::get);
    }

    public OptionalEntryStream<K, V> nonNullKey() {
        return filterKey(Objects::nonNull);
    }

    public OptionalEntryStream<K, V> nonNullValue() {
        return filterValue(Objects::nonNull);
    }

    public OptionalEntryStream<K, V> emptyValue() {
        return filterValue(Optional::isEmpty);
    }

    public OptionalEntryStream<K, V> presentValue() {
        return filterValue(Optional::isPresent);
    }

    public Stream<K> ensureEmpty() {
        return emptyValue().keys();
    }

    public EntryStream<K, V> ensurePresent() {
        return presentValue().get();
    }

    public OptionalEntryStream<K, V> sequential() {
        return new OptionalEntryStream<>(stream.sequential());
    }

    public EntryStream<K, V> orElse(V other) {
        return new EntryStream<>(stream).mapValue(v -> v.orElse(other));
    }

    public EntryStream<K, V> orElseGet(Supplier<V> supplier) {
        return new EntryStream<>(stream).mapValue(v -> v.orElseGet(supplier));
    }

    public EntryStream<K, V> orElseMap(Function<K, V> mapper) {
        return new EntryStream<>(stream).mapValue((k, v) -> v.orElse(mapper.apply(k)));
    }

    public <K2> OptionalEntryStream<K2, V> ensureKey(Class<K2> clazz) {
        return filterKey(clazz::isInstance).mapKey(clazz::cast);
    }

    public <V2> OptionalEntryStream<K, V2> ensureValue(Class<V2> clazz) {
        return filterPresentValue(clazz::isInstance).mapValue(clazz::cast);
    }

    public OptionalEntryStream<K, V> presentValue(BiPredicate<K, V> predicate) {
        return presentValue().filterPresent(predicate);
    }

    public OptionalEntryStream<K, V> presentValue(Predicate<V> predicate) {
        return presentValue().filterPresentValue(predicate);
    }

    public OptionalEntryStream<K, V> emptyKey(Predicate<K> predicate) {
        return emptyValue().filterKey(predicate);
    }

    public OptionalEntryStream<K, V> filter(BiPredicate<K, Optional<V>> filter) {
        return new OptionalEntryStream<>(stream.filter(entry -> filter.test(entry.getKey(), entry.getValue())));
    }

    public OptionalEntryStream<K, V> filterKey(Predicate<K> filter) {
        return new OptionalEntryStream<>(stream.filter(entry -> filter.test(entry.getKey())));
    }

    public OptionalEntryStream<K, V> filterValue(Predicate<Optional<V>> filter) {
        return new OptionalEntryStream<>(stream.filter(entry -> filter.test(entry.getValue())));
    }

    public OptionalEntryStream<K, V> filterPresent(BiPredicate<K, V> predicate) {
        return filter((k, v) -> v.isEmpty() || predicate.test(k, v.get()));
    }

    public OptionalEntryStream<K, V> filterPresentKey(Predicate<K> predicate) {
        return filter((k, v) -> v.isEmpty() || predicate.test(k));
    }

    public OptionalEntryStream<K, V> filterPresentValue(Predicate<V> predicate) {
        return filterValue(v -> v.isEmpty() || predicate.test(v.get()));
    }

    public <K2, V2> OptionalEntryStream<K2, V2> map(Function<K, K2> keyMapper, Function<V, V2> valueMapper) {
        return new OptionalEntryStream<>(stream.map(entry -> Map.entry(keyMapper.apply(entry.getKey()), entry.getValue().map(valueMapper))));
    }

    public <K2, V2> OptionalEntryStream<K2, V2> map(BiFunction<K, Optional<V>, K2> keyMapper, BiFunction<K, V, V2> valueMapper) {
        return new OptionalEntryStream<>(stream.map(entry -> Map.entry(keyMapper.apply(entry.getKey(), entry.getValue()), entry.getValue().map(v -> valueMapper.apply(entry.getKey(), v)))));
    }

    public <K2> OptionalEntryStream<K2, V> mapKey(Function<K, K2> keyMapper) {
        return new OptionalEntryStream<>(stream.map(entry -> Map.entry(keyMapper.apply(entry.getKey()), entry.getValue())));
    }

    public <K2> OptionalEntryStream<K2, V> mapKey(BiFunction<K, Optional<V>, K2> keyMapper) {
        return new OptionalEntryStream<>(stream.map(entry -> Map.entry(keyMapper.apply(entry.getKey(), entry.getValue()), entry.getValue())));
    }

    public <V2> OptionalEntryStream<K, V2> mapValue(Function<V, V2> valueMapper) {
        return new OptionalEntryStream<>(stream.map(entry -> Map.entry(entry.getKey(), entry.getValue().map(valueMapper))));
    }

    public <V2> OptionalEntryStream<K, V2> mapValue(BiFunction<K, V, V2> valueMapper) {
        return new OptionalEntryStream<>(stream.map(entry -> Map.entry(entry.getKey(), entry.getValue().map(v -> valueMapper.apply(entry.getKey(), v)))));
    }

    public <K2, V2> OptionalEntryStream<K2, V2> flatMap(BiFunction<K, Optional<V>, OptionalEntryStream<K2, V2>> mapper) {
        return new OptionalEntryStream<>(stream.flatMap(entry -> mapper.apply(entry.getKey(), entry.getValue()).stream));
    }

    public <K2, V2> OptionalEntryStream<K2, V2> flatMapKey(Function<K, OptionalEntryStream<K2, V2>> mapper) {
        return new OptionalEntryStream<>(stream.flatMap(entry -> mapper.apply(entry.getKey()).stream));
    }

    public <K2, V2> OptionalEntryStream<K2, V2> flatMapValue(Function<Optional<V>, OptionalEntryStream<K2, V2>> mapper) {
        return new OptionalEntryStream<>(stream.flatMap(entry -> mapper.apply(entry.getValue()).stream));
    }

    public <K2, V2> OptionalEntryStream<K2, V2> mapMulti(EntryStream.EntryMultiMapper<K, Optional<V>, K2, Optional<V2>> mapper) {
        return new OptionalEntryStream<>(stream.mapMulti((entry, buffer) -> mapper.mapMulti(entry.getKey(), entry.getValue(), (k2, v2) -> buffer.accept(Map.entry(k2, v2)))));
    }

    public <K2> OptionalEntryStream<K2, V> mapMultiKey(BiConsumer<K, Consumer<K2>> mapper) {
        return new OptionalEntryStream<>(stream.mapMulti((entry, buffer) -> mapper.accept(entry.getKey(), k2 -> buffer.accept(Map.entry(k2, entry.getValue())))));
    }

    public <V2> OptionalEntryStream<K, V2> mapMultiValue(BiConsumer<Optional<V>, Consumer<Optional<V2>>> mapper) {
        return new OptionalEntryStream<>(stream.mapMulti((entry, buffer) -> mapper.accept(entry.getValue(), v2 -> buffer.accept(Map.entry(entry.getKey(), v2)))));
    }

    public OptionalEntryStream<K, V> sortedKey(Comparator<K> comparator) {
        return new OptionalEntryStream<>(stream.sorted((entry1, entry2) -> comparator.compare(entry1.getKey(), entry2.getKey())));
    }

    public OptionalEntryStream<K, V> sortedValue(Comparator<Optional<V>> comparator) {
        return new OptionalEntryStream<>(stream.sorted((entry1, entry2) -> comparator.compare(entry1.getValue(), entry2.getValue())));
    }

    public OptionalEntryStream<K, V> peek(BiConsumer<K, Optional<V>> action) {
        return new OptionalEntryStream<>(stream.peek(entry -> action.accept(entry.getKey(), entry.getValue())));
    }

    public OptionalEntryStream<K, V> peekKey(Consumer<K> action) {
        return new OptionalEntryStream<>(stream.peek(entry -> action.accept(entry.getKey())));
    }

    public OptionalEntryStream<K, V> peekValue(Consumer<Optional<V>> action) {
        return new OptionalEntryStream<>(stream.peek(entry -> action.accept(entry.getValue())));
    }

    public OptionalEntryStream<K, V> peekPresent(BiConsumer<K, V> action) {
        return new OptionalEntryStream<>(stream.peek(entry -> entry.getValue().ifPresent(v -> action.accept(entry.getKey(), v))));
    }

    public OptionalEntryStream<K, V> peekPresentKey(Consumer<K> action) {
        return new OptionalEntryStream<>(stream.peek(entry -> entry.getValue().ifPresent(v -> action.accept(entry.getKey()))));
    }

    public OptionalEntryStream<K, V> peekPresentValue(Consumer<V> action) {
        return new OptionalEntryStream<>(stream.peek(entry -> entry.getValue().ifPresent(action)));
    }

    public OptionalEntryStream<K, V> peekEmptyKey(Consumer<K> action) {
        return new OptionalEntryStream<>(stream.peek(entry -> entry.getValue().ifPresentOrElse(Consumers.nop(), () -> action.accept(entry.getKey()))));
    }

    public OptionalEntryStream<K, V> limit(long maxSize) {
        return new OptionalEntryStream<>(stream.limit(maxSize));
    }

    public OptionalEntryStream<K, V> skip(long n) {
        return new OptionalEntryStream<>(stream.skip(n));
    }

    public OptionalEntryStream<K, V> takeWhile(BiPredicate<K, Optional<V>> filter) {
        return new OptionalEntryStream<>(stream.takeWhile(entry -> filter.test(entry.getKey(), entry.getValue())));
    }

    public OptionalEntryStream<K, V> takeWhileKey(Predicate<K> filter) {
        return new OptionalEntryStream<>(stream.takeWhile(entry -> filter.test(entry.getKey())));
    }

    public OptionalEntryStream<K, V> takeWhileValue(Predicate<Optional<V>> filter) {
        return new OptionalEntryStream<>(stream.takeWhile(entry -> filter.test(entry.getValue())));
    }

    public OptionalEntryStream<K, V> takeWhilePresent() {
        return takeWhileValue(Optional::isPresent);
    }

    public OptionalEntryStream<K, V> takeWhileEmpty() {
        return takeWhileValue(Optional::isEmpty);
    }

    public OptionalEntryStream<K, V> takeWhilePresentKey(Predicate<K> predicate) {
        return new OptionalEntryStream<>(stream.takeWhile(entry -> entry.getValue().isPresent() && predicate.test(entry.getKey())));
    }

    public OptionalEntryStream<K, V> takeWhilePresentValue(Predicate<V> predicate) {
        return new OptionalEntryStream<>(stream.takeWhile(entry -> entry.getValue().isPresent() && predicate.test(entry.getValue().get())));
    }

    public OptionalEntryStream<K, V> takeWhileEmptyKey(Predicate<K> predicate) {
        return new OptionalEntryStream<>(stream.takeWhile(entry -> entry.getValue().isEmpty() && predicate.test(entry.getKey())));
    }

    public OptionalEntryStream<K, V> dropWhile(BiPredicate<K, Optional<V>> predicate) {
        return new OptionalEntryStream<>(stream.dropWhile(entry -> predicate.test(entry.getKey(), entry.getValue())));
    }

    public OptionalEntryStream<K, V> dropWhileKey(Predicate<K> predicate) {
        return new OptionalEntryStream<>(stream.dropWhile(entry -> predicate.test(entry.getKey())));
    }

    public OptionalEntryStream<K, V> dropWhileValue(Predicate<Optional<V>> predicate) {
        return new OptionalEntryStream<>(stream.dropWhile(entry -> predicate.test(entry.getValue())));
    }

    public OptionalEntryStream<K, V> dropWhilePresent() {
        return dropWhileValue(Optional::isPresent);
    }

    public OptionalEntryStream<K, V> dropWhileEmpty() {
        return dropWhileValue(Optional::isEmpty);
    }

    public OptionalEntryStream<K, V> dropWhilePresentKey(Predicate<K> predicate) {
        return new OptionalEntryStream<>(stream.dropWhile(entry -> entry.getValue().isPresent() && predicate.test(entry.getKey())));
    }

    public OptionalEntryStream<K, V> dropWhilePresentValue(Predicate<V> predicate) {
        return new OptionalEntryStream<>(stream.dropWhile(entry -> entry.getValue().isPresent() && predicate.test(entry.getValue().get())));
    }

    public OptionalEntryStream<K, V> dropWhileEmptyKey(Predicate<K> predicate) {
        return new OptionalEntryStream<>(stream.dropWhile(entry -> entry.getValue().isEmpty() && predicate.test(entry.getKey())));
    }

    public void forEach(BiConsumer<K, Optional<V>> action) {
        stream.forEach(entry -> action.accept(entry.getKey(), entry.getValue()));
    }

    public void forEachKey(Consumer<K> action) {
        stream.forEach(entry -> action.accept(entry.getKey()));
    }

    public void forEachValue(Consumer<Optional<V>> action) {
        stream.forEach(entry -> action.accept(entry.getValue()));
    }

    public void forEachPresent(BiConsumer<K, V> action) {
        ensurePresent().forEach(action);
    }

    public void forEachPresentKey(Consumer<K> action) {
        presentValue().keys().forEach(action);
    }

    public void forEachPresentValue(Consumer<V> action) {
        ensurePresent().forEachValue(action);
    }

    public void forEachOrdered(BiConsumer<K, Optional<V>> action) {
        stream.forEachOrdered(entry -> action.accept(entry.getKey(), entry.getValue()));
    }

    public void forEachKeyOrdered(Consumer<K> action) {
        stream.forEachOrdered(entry -> action.accept(entry.getKey()));
    }

    public void forEachValueOrdered(Consumer<Optional<V>> action) {
        stream.forEachOrdered(entry -> action.accept(entry.getValue()));
    }

    public void forEachPresentOrdered(BiConsumer<K, V> action) {
        ensurePresent().forEachOrdered(action);
    }

    public void forEachPresentKeyOrdered(Consumer<K> action) {
        presentValue().keys().forEachOrdered(action);
    }

    public void forEachPresentValueOrdered(Consumer<V> action) {
        ensurePresent().values().forEachOrdered(action);
    }

    public void forEachEmptyKeyOrdered(Consumer<K> action) {
        ensureEmpty().forEachOrdered(action);
    }

    public K reduceKey(K identity, BinaryOperator<K> accumulator) {
        return keys().reduce(identity, accumulator);
    }

    public Optional<V> reduceValue(Optional<V> identity, BinaryOperator<Optional<V>> accumulator) {
        return values().reduce(identity, accumulator);
    }

    public K reducePresentKey(K identity, BinaryOperator<K> accumulator) {
        return ensurePresent().keys().reduce(identity, accumulator);
    }

    public V reducePresentValue(V identity, BinaryOperator<V> accumulator) {
        return ensurePresent().values().reduce(identity, accumulator);
    }

    public K reduceEmptyKey(K identity, BinaryOperator<K> accumulator) {
        return ensureEmpty().reduce(identity, accumulator);
    }

    public Optional<K> reduceKey(BinaryOperator<K> accumulator) {
        return keys().reduce(accumulator);
    }

    public Optional<V> reduceValue(BinaryOperator<Optional<V>> accumulator) {
        return values().reduce(accumulator).flatMap(Function.identity());
    }

    public Optional<K> reducePresentKey(BinaryOperator<K> accumulator) {
        return ensurePresent().keys().reduce(accumulator);
    }

    public Optional<V> reducePresentValue(BinaryOperator<V> accumulator) {
        return ensurePresent().values().reduce(accumulator);
    }

    public Optional<K> reduceEmptyKey(BinaryOperator<K> accumulator) {
        return ensureEmpty().reduce(accumulator);
    }

    public <R> R reduce(R identity, EntryStream.EntryReducer<R, K, Optional<V>> reducer) {
        return stream.sequential().reduce(identity, (r, entry) -> reducer.reduce(r, entry.getKey(), entry.getValue()), Unsupported.combinerOperator());
    }

    public <R> R reduceKey(R identity, BiFunction<R, K, R> reducer) {
        return stream.sequential().reduce(identity, (r, entry) -> reducer.apply(r, entry.getKey()), Unsupported.combinerOperator());
    }

    public <R> R reduceValue(R identity, BiFunction<R, Optional<V>, R> reducer) {
        return stream.sequential().reduce(identity, (r, entry) -> reducer.apply(r, entry.getValue()), Unsupported.combinerOperator());
    }

    public <R> R reducePresent(R identity, EntryStream.EntryReducer<R, K, V> reducer) {
        return stream.sequential().reduce(identity, (r, entry) -> entry.getValue().isPresent() ? reducer.reduce(r, entry.getKey(), entry.getValue().get()) : r, Unsupported.combinerOperator());
    }

    public <R> R reducePresentKey(R identity, BiFunction<R, K, R> reducer) {
        return stream.sequential().reduce(identity, (r, entry) -> entry.getValue().isPresent() ? reducer.apply(r, entry.getKey()) : r, Unsupported.combinerOperator());
    }

    public <R> R reducePresentValue(R identity, BiFunction<R, Optional<V>, R> reducer) {
        return stream.sequential().reduce(identity, (r, entry) -> entry.getValue().isPresent() ? reducer.apply(r, entry.getValue()) : r, Unsupported.combinerOperator());
    }

    public <R> R reduceEmptyKey(R identity, BiFunction<R, K, R> reducer) {
        return stream.sequential().reduce(identity, (r, entry) -> entry.getValue().isEmpty() ? reducer.apply(r, entry.getKey()) : r, Unsupported.combinerOperator());
    }

    public <R> R reduceParallel(R identity, EntryStream.EntryReducer<R, K, Optional<V>> reducer, BinaryOperator<R> combiner) {
        return stream.reduce(identity, (r, entry) -> reducer.reduce(r, entry.getKey(), entry.getValue()), combiner);
    }

    public <R> R reduceKeyParallel(R identity, BiFunction<R, K, R> reducer, BinaryOperator<R> combiner) {
        return stream.reduce(identity, (r, entry) -> reducer.apply(r, entry.getKey()), combiner);
    }

    public <R> R reduceValueParallel(R identity, BiFunction<R, Optional<V>, R> reducer, BinaryOperator<R> combiner) {
        return stream.reduce(identity, (r, entry) -> reducer.apply(r, entry.getValue()), combiner);
    }

    public <R> R reducePresentParallel(R identity, EntryStream.EntryReducer<R, K, V> reducer, BinaryOperator<R> combiner) {
        return stream.reduce(identity, (r, entry) -> entry.getValue().isPresent() ? reducer.reduce(r, entry.getKey(), entry.getValue().get()) : r, combiner);
    }

    public <R> R reducePresentKeyParallel(R identity, BiFunction<R, K, R> reducer, BinaryOperator<R> combiner) {
        return stream.reduce(identity, (r, entry) -> entry.getValue().isPresent() ? reducer.apply(r, entry.getKey()) : r, combiner);
    }

    public <R> R reducePresentValueParallel(R identity, BiFunction<R, V, R> reducer, BinaryOperator<R> combiner) {
        return stream.reduce(identity, (r, entry) -> entry.getValue().isPresent() ? reducer.apply(r, entry.getValue().get()) : r, combiner);
    }

    public <R> R reduceEmptyKeyParallel(R identity, BiFunction<R, K, R> reducer, BinaryOperator<R> combiner) {
        return stream.reduce(identity, (r, entry) -> entry.getValue().isEmpty() ? reducer.apply(r, entry.getKey()) : r, combiner);
    }

    public <R> R collect(Supplier<R> supplier, EntryStream.EntryAccumulator<R, K, Optional<V>> accumulator) {
        return stream.sequential().collect(supplier, (r, entry) -> accumulator.accumulate(r, entry.getKey(), entry.getValue()), Unsupported.combinerConsumer());
    }

    public <R> R collectKey(Supplier<R> supplier, BiConsumer<R, K> accumulator) {
        return stream.sequential().collect(supplier, (r, entry) -> accumulator.accept(r, entry.getKey()), Unsupported.combinerConsumer());
    }

    public <R> R collectValue(Supplier<R> supplier, BiConsumer<R, Optional<V>> accumulator) {
        return stream.sequential().collect(supplier, (r, entry) -> accumulator.accept(r, entry.getValue()), Unsupported.combinerConsumer());
    }

    public <R> R collectPresent(Supplier<R> supplier, EntryStream.EntryAccumulator<R, K, V> accumulator) {
        return stream.sequential().collect(supplier, (r, entry) -> entry.getValue().ifPresent(v -> accumulator.accumulate(r, entry.getKey(), v)), Unsupported.combinerConsumer());
    }

    public <R> R collectPresentKey(Supplier<R> supplier, BiConsumer<R, K> accumulator) {
        return stream.sequential().collect(supplier, (r, entry) -> entry.getValue().ifPresent(v -> accumulator.accept(r, entry.getKey())), Unsupported.combinerConsumer());
    }

    public <R> R collectPresentValue(Supplier<R> supplier, BiConsumer<R, V> accumulator) {
        return stream.sequential().collect(supplier, (r, entry) -> entry.getValue().ifPresent(v -> accumulator.accept(r, v)), Unsupported.combinerConsumer());
    }

    public <R> R collectEmptyKey(Supplier<R> supplier, BiConsumer<R, K> accumulator) {
        return stream.sequential().collect(supplier, (r, entry) -> entry.getValue().ifPresentOrElse(Consumers.nop(), () -> accumulator.accept(r, entry.getKey())), Unsupported.combinerConsumer());
    }

    public <R> R collectParallel(Supplier<R> supplier, EntryStream.EntryAccumulator<R, K, Optional<V>> accumulator, BiConsumer<R, R> combiner) {
        return stream.collect(supplier, (r, entry) -> accumulator.accumulate(r, entry.getKey(), entry.getValue()), combiner);
    }

    public <R> R collectKeyParallel(Supplier<R> supplier, BiConsumer<R, K> accumulator, BiConsumer<R, R> combiner) {
        return stream.collect(supplier, (r, entry) -> accumulator.accept(r, entry.getKey()), combiner);
    }

    public <R> R collectValueParallel(Supplier<R> supplier, BiConsumer<R, Optional<V>> accumulator, BiConsumer<R, R> combiner) {
        return stream.collect(supplier, (r, entry) -> accumulator.accept(r, entry.getValue()), combiner);
    }

    public <R> R collectPresentParallel(Supplier<R> supplier, EntryStream.EntryAccumulator<R, K, V> accumulator, BiConsumer<R, R> combiner) {
        return stream.collect(supplier, (r, entry) -> entry.getValue().ifPresent(v -> accumulator.accumulate(r, entry.getKey(), v)), combiner);
    }

    public <R> R collectPresentKeyParallel(Supplier<R> supplier, BiConsumer<R, K> accumulator, BiConsumer<R, R> combiner) {
        return stream.collect(supplier, (r, entry) -> entry.getValue().ifPresent(v -> accumulator.accept(r, entry.getKey())), combiner);
    }

    public <R> R collectPresentValueParallel(Supplier<R> supplier, BiConsumer<R, V> accumulator, BiConsumer<R, R> combiner) {
        return stream.collect(supplier, (r, entry) -> entry.getValue().ifPresent(v -> accumulator.accept(r, v)), combiner);
    }

    public <R> R collectEmptyKeyParallel(Supplier<R> supplier, BiConsumer<R, K> accumulator, BiConsumer<R, R> combiner) {
        return stream.collect(supplier, (r, entry) -> entry.getValue().ifPresentOrElse(Consumers.nop(), () -> accumulator.accept(r, entry.getKey())), combiner);
    }

    public Map<K, Optional<V>> toMap() {
        return stream.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<K, V> toPresentMap() {
        return ensurePresent().toMap();
    }

    public <M extends Map<K, Optional<V>>> M toMap(Supplier<M> supplier) {
        return stream.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2, supplier));
    }

    public <M extends Map<K, V>> M toPresentMap(Supplier<M> supplier) {
        return ensurePresent().toMap(supplier);
    }

    public SequencedMap<K, Optional<V>> toSequencedMap() {
        return stream.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2, LinkedHashMap::new));
    }

    public SequencedMap<K, V> toPresentSequencedMap() {
        return ensurePresent().toSequencedMap();
    }

    public <R> R toMap(Function<Map<K, Optional<V>>, R> function) {
        return function.apply(toMap());
    }

    public <R> R toPresentMap(Function<Map<K, V>, R> function) {
        return function.apply(toPresentMap());
    }

    public <R> R toSequencedMap(Function<SequencedMap<K, Optional<V>>, R> function) {
        return function.apply(toSequencedMap());
    }

    public <R> R toPresentSequencedMap(Function<SequencedMap<K, V>, R> function) {
        return function.apply(toPresentSequencedMap());
    }

    public Optional<Map.Entry<K, V>> minByKey(Comparator<K> keyComparator) {
        return stream.min((entry1, entry2) -> keyComparator.compare(entry1.getKey(), entry2.getKey())).flatMap(entry -> entry.getValue().map(v -> Map.entry(entry.getKey(), v)));
    }

    public Optional<Map.Entry<K, V>> minByValue(Comparator<Optional<V>> valueComparator) {
        return stream.max((entry1, entry2) -> valueComparator.compare(entry1.getValue(), entry2.getValue())).flatMap(entry -> entry.getValue().map(v -> Map.entry(entry.getKey(), v)));
    }

    public Optional<Map.Entry<K, V>> minByPresentValue(Comparator<V> valueComparator) {
        return ensurePresent().minByValue(valueComparator);
    }

    public Optional<K> minKeyByKey(Comparator<K> keyComparator) {
        return minByKey(keyComparator).map(Map.Entry::getKey);
    }

    public Optional<V> minValueByKey(Comparator<K> keyComparator) {
        return minByKey(keyComparator).map(Map.Entry::getValue);
    }

    public Optional<K> minKeyByValue(Comparator<Optional<V>> valueComparator) {
        return minByValue(valueComparator).map(Map.Entry::getKey);
    }

    public Optional<V> minValueByValue(Comparator<Optional<V>> valueComparator) {
        return minByValue(valueComparator).map(Map.Entry::getValue);
    }

    public Optional<K> minKeyByPresentValue(Comparator<V> valueComparator) {
        return minByPresentValue(valueComparator).map(Map.Entry::getKey);
    }

    public Optional<V> minValueByPresentValue(Comparator<V> valueComparator) {
        return minByPresentValue(valueComparator).map(Map.Entry::getValue);
    }

    public Optional<Map.Entry<K, V>> maxByKey(Comparator<K> keyComparator) {
        return stream.max((entry1, entry2) -> keyComparator.compare(entry1.getKey(), entry2.getKey())).flatMap(entry -> entry.getValue().map(v -> Map.entry(entry.getKey(), v)));
    }

    public Optional<Map.Entry<K, V>> maxByValue(Comparator<Optional<V>> valueComparator) {
        return stream.max((entry1, entry2) -> valueComparator.compare(entry1.getValue(), entry2.getValue())).flatMap(entry -> entry.getValue().map(v -> Map.entry(entry.getKey(), v)));
    }

    public Optional<Map.Entry<K, V>> maxByPresentValue(Comparator<V> valueComparator) {
        return ensurePresent().maxByValue(valueComparator);
    }

    public Optional<K> maxKeyByKey(Comparator<K> keyComparator) {
        return maxByKey(keyComparator).map(Map.Entry::getKey);
    }

    public Optional<V> maxValueByKey(Comparator<K> keyComparator) {
        return maxByKey(keyComparator).map(Map.Entry::getValue);
    }

    public Optional<K> maxKeyByValue(Comparator<Optional<V>> valueComparator) {
        return maxByValue(valueComparator).map(Map.Entry::getKey);
    }

    public Optional<V> maxValueByValue(Comparator<Optional<V>> valueComparator) {
        return maxByValue(valueComparator).map(Map.Entry::getValue);
    }

    public Optional<K> maxKeyByPresentValue(Comparator<V> valueComparator) {
        return maxByPresentValue(valueComparator).map(Map.Entry::getKey);
    }

    public Optional<V> maxValueByPresentValue(Comparator<V> valueComparator) {
        return maxByPresentValue(valueComparator).map(Map.Entry::getValue);
    }

    public boolean anyMatch(BiPredicate<K, Optional<V>> filter) {
        return stream.anyMatch(entry -> filter.test(entry.getKey(), entry.getValue()));
    }

    public boolean anyMatchKey(Predicate<K> filter) {
        return stream.anyMatch(entry -> filter.test(entry.getKey()));
    }

    public boolean anyMatchValue(Predicate<Optional<V>> filter) {
        return stream.anyMatch(entry -> filter.test(entry.getValue()));
    }

    public boolean anyMatchPresent() {
        return anyMatchValue(Optional::isPresent);
    }

    public boolean anyMatchPresent(BiPredicate<K, V> filter) {
        return stream.anyMatch(entry -> entry.getValue().isPresent() && filter.test(entry.getKey(), entry.getValue().get()));
    }

    public boolean anyMatchPresentKey(Predicate<K> filter) {
        return stream.anyMatch(entry -> entry.getValue().isPresent() && filter.test(entry.getKey()));
    }

    public boolean anyMatchPresentValue(Predicate<V> filter) {
        return stream.anyMatch(entry -> entry.getValue().isPresent() && filter.test(entry.getValue().get()));
    }

    public boolean anyMatchEmpty() {
        return anyMatchValue(Optional::isEmpty);
    }

    public boolean anyMatchEmptyKey(Predicate<K> filter) {
        return stream.anyMatch(entry -> entry.getValue().isEmpty() && filter.test(entry.getKey()));
    }

    public boolean allMatch(BiPredicate<K, Optional<V>> filter) {
        return stream.allMatch(entry -> filter.test(entry.getKey(), entry.getValue()));
    }

    public boolean allMatchKey(Predicate<K> filter) {
        return stream.allMatch(entry -> filter.test(entry.getKey()));
    }

    public boolean allMatchValue(Predicate<Optional<V>> filter) {
        return stream.allMatch(entry -> filter.test(entry.getValue()));
    }

    public boolean allMatchPresent() {
        return allMatchValue(Optional::isPresent);
    }

    public boolean allMatchPresent(BiPredicate<K, V> filter) {
        return stream.allMatch(entry -> entry.getValue().isPresent() && filter.test(entry.getKey(), entry.getValue().get()));
    }

    public boolean allMatchPresentKey(Predicate<K> filter) {
        return stream.allMatch(entry -> entry.getValue().isPresent() && filter.test(entry.getKey()));
    }

    public boolean allMatchPresentValue(Predicate<V> filter) {
        return stream.allMatch(entry -> entry.getValue().isPresent() && filter.test(entry.getValue().get()));
    }

    public boolean allMatchEmpty() {
        return allMatchValue(Optional::isEmpty);
    }

    public boolean allMatchEmptyKey(Predicate<K> filter) {
        return stream.allMatch(entry -> entry.getValue().isEmpty() && filter.test(entry.getKey()));
    }

    public boolean noneMatch(BiPredicate<K, Optional<V>> filter) {
        return stream.noneMatch(entry -> filter.test(entry.getKey(), entry.getValue()));
    }

    public boolean noneMatchKey(Predicate<K> filter) {
        return stream.noneMatch(entry -> filter.test(entry.getKey()));
    }

    public boolean noneMatchValue(Predicate<Optional<V>> filter) {
        return stream.anyMatch(entry -> filter.test(entry.getValue()));
    }

    public boolean noneMatchPresent() {
        return noneMatchValue(Optional::isPresent);
    }

    public boolean noneMatchPresent(BiPredicate<K, V> filter) {
        return stream.noneMatch(entry -> entry.getValue().isPresent() && filter.test(entry.getKey(), entry.getValue().get()));
    }

    public boolean noneMatchPresentKey(Predicate<K> filter) {
        return stream.noneMatch(entry -> entry.getValue().isPresent() && filter.test(entry.getKey()));
    }

    public boolean noneMatchPresentValue(Predicate<V> filter) {
        return stream.noneMatch(entry -> entry.getValue().isPresent() && filter.test(entry.getValue().get()));
    }

    public boolean noneMatchEmpty() {
        return noneMatchValue(Optional::isEmpty);
    }

    public boolean noneMatchEmptyKey(Predicate<K> filter) {
        return stream.noneMatch(entry -> entry.getValue().isEmpty() && filter.test(entry.getKey()));
    }

    public Optional<Map.Entry<K, Optional<V>>> findFirst() {
        return stream.findFirst();
    }

    public void findFirst(BiConsumer<K, Optional<V>> action) {
        stream.findFirst().ifPresent(entry -> action.accept(entry.getKey(), entry.getValue()));
    }

    public <R> Optional<R> mapFirst(BiFunction<K, Optional<V>, R> mapper) {
        return stream.findFirst().map(entry -> mapper.apply(entry.getKey(), entry.getValue()));
    }

    public Optional<K> findFirstKey() {
        return stream.findFirst().map(Map.Entry::getKey);
    }

    public Optional<V> findFirstValue() {
        return stream.findFirst().map(Map.Entry::getValue).flatMap(Function.identity());
    }

    public Optional<Map.Entry<K, Optional<V>>> findAny() {
        return stream.findAny();
    }

    public void findAny(BiConsumer<K, Optional<V>> action) {
        stream.findAny().ifPresent(entry -> action.accept(entry.getKey(), entry.getValue()));
    }

    public <R> Optional<R> mapAny(BiFunction<K, Optional<V>, R> mapper) {
        return stream.findAny().map(entry -> mapper.apply(entry.getKey(), entry.getValue()));
    }

    public Optional<K> findAnyKey() {
        return stream.findAny().map(Map.Entry::getKey);
    }

    public Optional<V> findAnyValue() {
        return stream.findAny().map(Map.Entry::getValue).flatMap(Function.identity());
    }

    public static <K, V> OptionalEntryStream<K, V> empty() {
        return new OptionalEntryStream<>(Stream.empty());
    }

    public static <K, V> OptionalEntryStream<K, V> of(EntryStream<K, Optional<V>> entryStream) {
        return new OptionalEntryStream<>(entryStream);
    }

    public static <K, V> OptionalEntryStream<K, V> concat(OptionalEntryStream<K, V> a, OptionalEntryStream<K, V> b) {
        return new OptionalEntryStream<>(Stream.concat(a.stream, b.stream));
    }

    public static <K, V> OptionalEntryStream<K, V> fromMap(Map<K, Optional<V>> map) {
        return new OptionalEntryStream<>(map.entrySet().stream());
    }

    public static <K, V> OptionalEntryStream<K, V> fromSequencedMap(SequencedMap<K, Optional<V>> map) {
        return new OptionalEntryStream<>(map.sequencedEntrySet().stream());
    }

    public static <K, V> OptionalEntryStream<K, V> fromKeys(Stream<K> stream, Function<K, Optional<V>> valueMapper) {
        return new OptionalEntryStream<>(stream.map(k -> Map.entry(k, valueMapper.apply(k))));
    }

    public static <K, V> OptionalEntryStream<K, V> fromKeys(List<K> list, Function<K, Optional<V>> valueMapper) {
        return new OptionalEntryStream<>(list.stream().map(k -> Map.entry(k, valueMapper.apply(k))));
    }

    public static <K, V> OptionalEntryStream<K, V> fromKeys(Iterator<K> iterator, Function<K, Optional<V>> valueMapper) {
        return new OptionalEntryStream<>(Streams.of(iterator).map(k -> Map.entry(k, valueMapper.apply(k))));
    }

    public static <K, V> OptionalEntryStream<K, V> fromValues(Stream<Optional<V>> stream, Function<Optional<V>, K> keyMapper) {
        return new OptionalEntryStream<>(stream.map(v -> Map.entry(keyMapper.apply(v), v)));
    }

    public static <K, V> OptionalEntryStream<K, V> fromValues(List<Optional<V>> list, Function<Optional<V>, K> keyMapper) {
        return new OptionalEntryStream<>(list.stream().map(v -> Map.entry(keyMapper.apply(v), v)));
    }

    public static <K, V> OptionalEntryStream<K, V> fromValues(Iterator<Optional<V>> iterator, Function<Optional<V>, K> keyMapper) {
        return new OptionalEntryStream<>(Streams.of(iterator).map(v -> Map.entry(keyMapper.apply(v), v)));
    }

    public static <R, K, V> OptionalEntryStream<K, V> fromStream(Stream<R> stream, Function<R, K> keyMapper, Function<R, Optional<V>> valueMapper) {
        return new OptionalEntryStream<>(stream.map(r -> Map.entry(keyMapper.apply(r), valueMapper.apply(r))));
    }
}
