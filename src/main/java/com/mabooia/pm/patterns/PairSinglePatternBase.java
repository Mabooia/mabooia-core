package com.mabooia.pm.patterns;

import com.mabooia.ObjectsEx;
import java.util.Map;

public abstract class PairSinglePatternBase<A> implements SinglePattern<A> {

    private final SinglePattern<A> singlePattern;
    private final Pattern pattern;

    protected PairSinglePatternBase(final SinglePattern<A> singlePattern,
                                    final Pattern pattern) {
        this.singlePattern = singlePattern;
        this.pattern = pattern;
    }

    @Override
    public boolean match(final Object expr) {
        if (ObjectsEx.is(Map.Entry.class, expr)) {
            final Map.Entry<?, ?> pair = (Map.Entry<?, ?>)expr;
            return singlePattern.match(getCurrentLegValueFrom(pair))
                && pattern.match(getAnotherLegValueFrom(pair));
        }

        return false;
    }

    @Override
    public A getMatchedArg(final Object expr) {
        final Map.Entry<?, ?> pair = (Map.Entry<?, ?>)expr;
        @SuppressWarnings("unchecked")
        final A arg = (A) getCurrentLegValueFrom(pair);
        return arg;
    }

    protected abstract Object getCurrentLegValueFrom(final Map.Entry<?, ?> entry);
    protected abstract Object getAnotherLegValueFrom(final Map.Entry<?, ?> entry);
}
