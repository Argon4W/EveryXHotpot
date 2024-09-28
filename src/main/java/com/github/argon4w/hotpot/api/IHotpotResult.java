package com.github.argon4w.hotpot.api;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public interface IHotpotResult<T> {
    IHotpotResult<T> consume(Consumer<T> consumer);
    <R> IHotpotResult<R> map(Function<T, R> function);
    IHotpotResult<T> ifPresent(Consumer<T> consumer);
    IHotpotResult<T> ifEmpty(Runnable runnable);
    boolean isPresent();
    boolean isEmpty();
    boolean isBlocked();
    Optional<T> getOptional();
    T get();
    T orElse(T other);

    abstract class Empty<T> implements IHotpotResult<T> {
        @Override
        public IHotpotResult<T> consume(Consumer<T> consumer) {
            return this;
        }

        @Override
        public <R> IHotpotResult<R> map(Function<T, R> function) {
            return IHotpotResult.pass();
        }

        @Override
        public IHotpotResult<T> ifPresent(Consumer<T> consumer) {
            return this;
        }

        @Override
        public IHotpotResult<T> ifEmpty(Runnable runnable) {
            runnable.run();
            return this;
        }

        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public Optional<T> getOptional() {
            return Optional.empty();
        }

        @Override
        public T get() {
            throw new IllegalStateException("Illegal call to a empty result");
        }

        @Override
        public T orElse(T other) {
            return other;
        }
    }

    class BlockedEmpty<T> extends Empty<T> {
        @Override
        public boolean isBlocked() {
            return true;
        }
    }

    class NonBlockedEmpty<T> extends Empty<T> {
        @Override
        public boolean isBlocked() {
            return false;
        }
    }

    class Value<T> implements IHotpotResult<T> {
        private final T value;

        public Value(T value) {
            this.value = Objects.requireNonNull(value);
        }

        @Override
        public IHotpotResult<T> consume(Consumer<T> consumer) {
            consumer.accept(value);
            return IHotpotResult.pass();
        }

        @Override
        public <R> IHotpotResult<R> map(Function<T, R> function) {
            return IHotpotResult.ofNullable(function.apply(value));
        }

        @Override
        public IHotpotResult<T> ifPresent(Consumer<T> consumer) {
            consumer.accept(value);
            return this;
        }

        @Override
        public IHotpotResult<T> ifEmpty(Runnable runnable) {
            return this;
        }

        @Override
        public boolean isPresent() {
            return true;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean isBlocked() {
            return false;
        }

        @Override
        public Optional<T> getOptional() {
            return Optional.of(value);
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public T orElse(T other) {
            return value;
        }
    }

    static <T> IHotpotResult<T> ofNullable(T value) {
        return value == null ? pass() : success(value);
    }

    static <T> IHotpotResult<T> pass() {
        return new NonBlockedEmpty<>();
    }

    static <T> IHotpotResult<T> blocked() {
        return new BlockedEmpty<>();
    }

    static <T> IHotpotResult<T> success(T value) {
        return new Value<>(value);
    }
}
