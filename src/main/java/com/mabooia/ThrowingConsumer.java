package com.mabooia;

import java.util.function.Function;
import java.util.function.Supplier;

@FunctionalInterface
public interface ThrowingConsumer<T, E extends Exception> {

    void accept(final T arg) throws E;

    default ThrowingRunnable<E> compose(final Supplier<? extends T> g) {
        return () -> accept(g.get());
    }

    default ThrowingRunnable<Exception> compose(final ThrowingSupplier<? extends T, ? extends Exception> g) {
        return () -> accept(g.get());
    }

    default <R> ThrowingConsumer<R, E> compose(final Function<? super R, ? extends T> g) {
        return arg -> accept(g.apply(arg));
    }

    default <R> ThrowingConsumer<R, Exception> compose(final ThrowingFunction<? super R, ? extends T, ? extends Exception> g) {
        return arg -> accept(g.apply(arg));
    }

    default ThrowingRunnable<E> curry(final T arg) {
        return () -> accept(arg);
    }
}
