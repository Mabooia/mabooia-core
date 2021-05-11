package com.mabooia.pm.patterns;

public final class Patterns {

    public static Pattern any() {
        return new AnyPattern();
    }

    public static Pattern eq(final Object value) {
        return new EqualsToPattern(value);
    }

    public static Pattern nul() {
        return eq(null);
    }

    public static <A, B> DoublePattern<A, B> pair(final SinglePattern<A> aPattern, final SinglePattern<B> bPattern) {
        return new PairDoublePattern<>(aPattern, bPattern);
    }

    public static <A> SinglePattern<A> pair(final SinglePattern<A> left, final Pattern right) {
        return new PairLeftPattern<>(left, right);
    }

    public static <A> SinglePattern<A> pair(final Pattern left, final SinglePattern<A> right) {
        return new PairRightPattern<>(left, right);
    }

    public static Pattern pair(final Pattern left, final Pattern right) {
        return new PairVoidPattern(left, right);
    }

    public static <A> SinglePattern<A> ofType(final Class<A> aClass) {
        return new OfTypePattern<>(aClass);
    }

    public static Pattern succ(final Pattern pattern) {
        return new IsSuccessPattern(pattern);
    }

    public static <A> SinglePattern<A> succ(final SinglePattern<A> pattern) {
        return new IsSuccessSinglePattern<>(pattern);
    }

    private Patterns() {
    }
}
