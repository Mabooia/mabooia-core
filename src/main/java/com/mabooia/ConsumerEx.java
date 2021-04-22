package com.mabooia;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@FunctionalInterface
public interface ConsumerEx<T> extends Consumer<T> {

    default Runnable compose(final Supplier<? extends T> supplier) {
        Objects.requireNonNull(supplier);
        return () -> accept(supplier.get());
    }

    default <A> ConsumerEx<A> compose(final Function<? super A, ? extends T> f) {
        Objects.requireNonNull(f);
        return arg -> accept(f.apply(arg));
    }

    default Runnable curry(final T arg) {
        return () -> accept(arg);
    }
}
