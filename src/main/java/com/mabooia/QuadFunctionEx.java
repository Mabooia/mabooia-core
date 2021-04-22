package com.mabooia;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface QuadFunctionEx<A, B, C, D, R> {
    R apply(A a, B b, C c, D d);

    default <V> QuadFunctionEx<A, B, C, D, V> andThen(final Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (A a, B b, C c, D d) -> after.apply(apply(a, b, c, d));
    }

    default TriFunctionEx<B, C, D, R> curry(final A a) {
        return (b, c, d) -> apply(a, b, c, d);
    }
}
