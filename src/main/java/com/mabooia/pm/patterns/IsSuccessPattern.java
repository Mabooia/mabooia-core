package com.mabooia.pm.patterns;

public class IsSuccessPattern extends IsSuccessPatternBase implements Pattern {

    private final Pattern pattern;

    public IsSuccessPattern(final Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    protected boolean patternMatch(Object expr) {
        return pattern.match(expr);
    }
}
