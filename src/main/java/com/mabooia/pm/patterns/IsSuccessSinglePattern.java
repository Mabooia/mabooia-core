package com.mabooia.pm.patterns;

import com.mabooia.Try;

public class IsSuccessSinglePattern<A> extends IsSuccessPatternBase implements SinglePattern<A> {

    private final SinglePattern<A> pattern;

    public IsSuccessSinglePattern(SinglePattern<A> pattern) {
        this.pattern = pattern;
    }

    @Override
    public A getMatchedArg(final Object expr) {
        return ((Try<?>)expr)
            .toOptional()
            .map(pattern::getMatchedArg)
            .orElse(null);
    }

    @Override
    protected boolean patternMatch(Object expr) {
        return pattern.match(expr);
    }
}
