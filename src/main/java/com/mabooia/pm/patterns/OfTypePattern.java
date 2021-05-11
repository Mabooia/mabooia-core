package com.mabooia.pm.patterns;

import com.mabooia.ObjectsEx;

public class OfTypePattern<A> implements SinglePattern<A> {

    private final Class<A> type;

    OfTypePattern(final Class<A> type) {
        this.type = type;
    }

    @Override
    public boolean match(final Object expr) {
        return ObjectsEx.is(type, expr);
    }

    @Override
    public A getMatchedArg(final Object expr) {
        return ObjectsEx.as(type, expr);
    }
}
