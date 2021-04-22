package com.mabooia;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface TriFunctionEx<A, B, C, R> {

    R apply(A a, B b, C c);

    default <V> TriFunctionEx<A, B, C, V> andThen(final Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (A a, B b, C c) -> after.apply(apply(a, b, c));
    }

    default BiFunctionEx<B, C, R> curry(final A a) {
        return (b, c) -> apply(a, b, c);
    }
}
