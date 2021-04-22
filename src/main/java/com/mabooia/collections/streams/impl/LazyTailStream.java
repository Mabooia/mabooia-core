package com.mabooia.collections.streams.impl;

import com.mabooia.Lazy;
import com.mabooia.collections.streams.Stream;

public final class LazyTailStream<A> extends NonEmptyStream<A> {

    private final Lazy<Stream<A>> lazyTail;

    public LazyTailStream(final A head, final Lazy<Stream<A>> lazyTail) {
        super(head);
        this.lazyTail = lazyTail;
    }

    @Override
    public Stream<A> getTail() {
        return lazyTail.get();
    }

    @Override
    public String toString() {
        final String tailString = lazyTail
            .getIfComputed()
            .map(Object::toString)
            .orElse("...");

        return String.format("%s, %s", getHead(), tailString);
    }
}
