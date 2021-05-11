package com.mabooia.pm.patterns;

import java.util.Map;

public class PairRightPattern<A> extends PairSinglePatternBase<A> {

    public PairRightPattern(final Pattern pattern,
                            final SinglePattern<A> singlePattern) {
        super(singlePattern, pattern);
    }

    @Override
    protected Object getCurrentLegValueFrom(final Map.Entry<?, ?> entry) {
        return entry.getValue();
    }

    @Override
    protected Object getAnotherLegValueFrom(final Map.Entry<?, ?> entry) {
        return entry.getKey();
    }
}
