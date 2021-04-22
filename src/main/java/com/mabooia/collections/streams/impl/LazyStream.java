package com.mabooia.collections.streams.impl;

import com.mabooia.Lazy;
import com.mabooia.collections.streams.Stream;

import java.util.Optional;

public final class LazyStream<A> implements Stream<A> {

    private final Lazy<Stream<A>> lazyStream;

    public LazyStream(final Lazy<Stream<A>> lazyStream) {
        this.lazyStream = lazyStream;
    }

    @Override
    public Optional<A> getHeadIfPresent() {
        return lazyStream.get().getHeadIfPresent();
    }

    @Override
    public Stream<A> getTail() {
        return lazyStream.get().getTail();
    }

    @Override
    public String toString() {
        return lazyStream
            .getIfComputed()
            .map(Stream::toString)
            .orElse("...");
    }
}
