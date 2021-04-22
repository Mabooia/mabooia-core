package com.mabooia;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@FunctionalInterface
public interface FunctionEx<T, R> extends Function<T, R> {

    default ConsumerEx<T> andFinally(final Consumer<R> consumer) {
        Objects.requireNonNull(consumer);
        return arg -> consumer.accept(apply(arg));
    }

    default <V> FunctionEx<T, V> andThen(final Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return arg -> after.apply(apply(arg));
    }

    default SupplierEx<R> compose(final Supplier<? extends T> supplier) {
        Objects.requireNonNull(supplier);
        return () -> apply(supplier.get());
    }

    default <V> FunctionEx<V, R> compose(final Function<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return arg -> apply(before.apply(arg));
    }

    default SupplierEx<R> curry(final T arg) {
        return () -> apply(arg);
    }

    static <T> FunctionEx<T, T> identity() {
        return arg -> arg;
    }
}
