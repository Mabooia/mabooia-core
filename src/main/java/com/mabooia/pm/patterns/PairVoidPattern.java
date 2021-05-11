package com.mabooia.pm.patterns;

import com.mabooia.ObjectsEx;
import java.util.Map;

public class PairVoidPattern implements Pattern {

    private final Pattern leftPattern;
    private final Pattern rightPattern;

    public PairVoidPattern(final Pattern leftPattern, final Pattern rightPattern) {
        this.leftPattern = leftPattern;
        this.rightPattern = rightPattern;
    }

    @Override
    public boolean match(final Object expr) {
        if (ObjectsEx.is(Map.Entry.class, expr)) {
            final Map.Entry<?, ?> pair = (Map.Entry<?, ?>)expr;
            return leftPattern.match(pair.getKey())
                && rightPattern.match(pair.getValue());
        }

        return false;
    }
}
