package com.mabooia.collections.streams.impl;

import com.mabooia.collections.streams.Stream;
import java.util.Objects;
import java.util.Optional;

public abstract class NonEmptyStream<A> implements Stream<A> {

    private final A head;

    protected NonEmptyStream(final A head) {
        Objects.requireNonNull(head);
        this.head = head;
    }

    public A getHead() {
        return head;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean nonEmpty() {
        return true;
    }

    @Override
    public Optional<A> getHeadIfPresent() {
        return Optional.of(head);
    }
}
