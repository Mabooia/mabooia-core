package com.mabooia.pm.patterns;

import com.mabooia.ObjectsEx;
import java.util.Map;

public class PairDoublePattern<A, B> implements DoublePattern<A, B> {

    private final SinglePattern<A> aPattern;
    private final SinglePattern<B> bPattern;

    PairDoublePattern(final SinglePattern<A> aPattern, final SinglePattern<B> bPattern) {
        this.aPattern = aPattern;
        this.bPattern = bPattern;
    }

    @Override
    public boolean match(final Object expr) {
        if (ObjectsEx.is(Map.Entry.class, expr)) {
            final Map.Entry<?, ?> pair = (Map.Entry<?, ?>)expr;
            return aPattern.match(pair.getKey())
                && bPattern.match(pair.getValue());
        }

        return false;
    }

    @Override
    public A getFirstMatchedArg(final Object expr) {
        final Map.Entry<?, ?> pair = (Map.Entry<?, ?>)expr;
        @SuppressWarnings("unchecked")
        final A arg = (A)pair.getKey();
        return arg;
    }

    @Override
    public B getSecondMatchedArg(final Object expr) {
        final Map.Entry<?, ?> pair = (Map.Entry<?, ?>)expr;
        @SuppressWarnings("unchecked")
        final B arg = (B)pair.getValue();
        return arg;
    }
}
