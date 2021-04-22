package com.mabooia.collections.streams.impl;

import com.mabooia.collections.streams.Stream;

public final class ConsStream<A> extends NonEmptyStream<A> {

    private final Stream<A> tail;

    public ConsStream(final A head, final Stream<A> tail) {
        super(head);
        this.tail = tail;
    }

    @Override
    public Stream<A> getTail() {
        return tail;
    }

    @Override
    public String toString() {
        return String.format("%s, %s", getHead(), getTail());
    }
}
