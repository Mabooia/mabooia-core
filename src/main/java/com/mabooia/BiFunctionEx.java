package com.mabooia;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

@FunctionalInterface
public interface BiFunctionEx<A, B, R> extends BiFunction<A, B, R> {

    default <V> BiFunctionEx<A, B, V> andThen(final Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (A a, B b) -> after.apply(apply(a, b));
    }

    default FunctionEx<B, R> curry(final A a) {
        return b -> apply(a, b);
    }
}
