package com.mabooia;

import java.util.function.Predicate;

@FunctionalInterface
public interface PredicateEx<T> extends FunctionEx<T, Boolean>, Predicate<T> {

    default Boolean apply(T arg) {
        return test(arg);
    }
}
