package com.mabooia;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Exception> {

    R apply(T arg) throws E;

    default <V> ThrowingFunction<T, V, E> andThen(final Function<? super R, ? extends V> g) {
        return arg -> g.apply(apply(arg));
    }

    default <V> ThrowingFunction<T, V, Exception> andThen(final ThrowingFunction<? super R, ? extends V, ? extends Exception> g) {
        return arg -> g.apply(apply(arg));
    }

    default ThrowingConsumer<T, E> andThen(final Consumer<? super R> g) {
        return arg -> g.accept(apply(arg));
    }

    default ThrowingConsumer<T, Exception> andThen(final ThrowingConsumer<? super R, ? extends Exception> g) {
        return arg -> g.accept(apply(arg));
    }

    default <V> ThrowingFunction<V, R, E> compose(final Function<? super V, ? extends T> g) {
        return arg -> apply(g.apply(arg));
    }

    default <V> ThrowingFunction<V, R, Exception> compose(final ThrowingFunction<? super V, ? extends T, ? extends Exception> g) {
        return arg -> apply(g.apply(arg));
    }

    default ThrowingSupplier<R, E> compose(final Supplier<? extends T> g) {
        return () -> apply(g.get());
    }

    default ThrowingSupplier<R, Exception> compose(final ThrowingSupplier<? extends T, ? extends Exception> g) {
        return () -> apply(g.get());
    }

    default ThrowingSupplier<R, E> curry(final T arg) {
        return () -> apply(arg);
    }

    static <T> ThrowingFunction<T, T, Exception> identity() {
        return arg -> arg;
    }
}
