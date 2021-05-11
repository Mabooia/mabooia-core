package com.mabooia.pm.patterns;

import java.util.Map;

public class PairLeftPattern<A> extends PairSinglePatternBase<A> {

    public PairLeftPattern(final SinglePattern<A> singlePattern,
                           final Pattern pattern) {
        super(singlePattern, pattern);
    }

    @Override
    protected Object getCurrentLegValueFrom(final Map.Entry<?, ?> entry) {
        return entry.getKey();
    }

    @Override
    protected Object getAnotherLegValueFrom(final Map.Entry<?, ?> entry) {
        return entry.getValue();
    }
}
