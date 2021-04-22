package com.mabooia;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@FunctionalInterface
public interface SupplierEx<T> extends Supplier<T> {

    default Runnable andFinally(final Consumer<? super T> consumer) {
        Objects.requireNonNull(consumer);
        return () -> consumer.accept(get());
    }

    default <R> SupplierEx<R> andThen(final Function<? super T, ? extends R> f) {
        Objects.requireNonNull(f);
        return () -> f.apply(get());
    }
}
