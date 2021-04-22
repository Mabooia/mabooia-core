package com.mabooia;

import java.util.function.BiPredicate;

@FunctionalInterface
public interface BiPredicateEx<A, B> extends BiFunctionEx<A, B, Boolean>, BiPredicate<A, B> {

    default Boolean apply(A a, B b) {
        return test(a, b);
    }
}
