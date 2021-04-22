package com.mabooia;

public interface ThrowingBiConsumer<A, B, E extends Exception> {

    void accept(final A a, final B b) throws E;

    default ThrowingConsumer<B, E> curry(final A a) {
        return b -> accept(a, b);
    }
}
