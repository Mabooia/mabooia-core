package com.mabooia;

import java.util.function.Consumer;
import java.util.function.Function;

@FunctionalInterface
public interface ThrowingSupplier<T, E extends Exception> {

    T get() throws E;

    default ThrowingRunnable<E> andThen(final Consumer<? super T> g) {
        return () -> g.accept(get());
    }

    default ThrowingRunnable<Exception> andThen(final ThrowingConsumer<? super T, ? extends Exception> g) {
        return () -> g.accept(get());
    }

    default <R> ThrowingSupplier<R, E> andThen(final Function<? super T, ? extends R> g) {
        return () -> g.apply(get());
    }

    default <R> ThrowingSupplier<R, Exception> andThen(final ThrowingFunction<? super T, ? extends R, ? extends Exception> g) {
        return () -> g.apply(get());
    }
}
