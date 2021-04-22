package com.mabooia;

@FunctionalInterface
public interface TriPredicate<A, B, C> extends TriFunctionEx<A, B, C, Boolean> {

    boolean test(A a, B b, C c);

    default Boolean apply(A a, B b, C c) {
        return test(a, b, c);
    }
}
