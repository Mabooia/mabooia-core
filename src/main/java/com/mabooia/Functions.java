package com.mabooia;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class Functions {

    public static <T> ConsumerEx<T> from(Consumer<T> consumer) {
        return consumer::accept;
    }

    public static <T> SupplierEx<T> from(Supplier<T> supplier) {
        return supplier::get;
    }

    public static <T, R> FunctionEx<T, R> from(Function<T, R> function) {
        return function::apply;
    }

    public static <T> PredicateEx<T> from(Predicate<T> predicate) {
        return predicate::test;
    }

    public static <A, B, R> BiFunctionEx<A, B, R> from(BiFunction<A, B, R> function) {
        return function::apply;
    }

    public static <A, B> BiPredicateEx<A, B> from(BiPredicate<A, B> biPredicate) {
        return biPredicate::test;
    }
}
