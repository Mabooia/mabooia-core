package com.mabooia.collections.streams.impl;

import com.mabooia.collections.streams.Stream;

import java.util.Optional;

public final class EmptyStream implements Stream<Object> {

    @Override
    public Optional<Object> getHeadIfPresent() {
        return Optional.empty();
    }

    @Override
    public Stream<Object> getTail() {
        return this;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean nonEmpty() {
        return false;
    }

    @Override
    public String toString() {
        return "";
    }
}
