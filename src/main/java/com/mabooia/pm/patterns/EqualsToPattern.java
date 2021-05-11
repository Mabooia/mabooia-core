package com.mabooia.pm.patterns;

import java.util.Objects;

public class EqualsToPattern implements Pattern {

    private final Object value;

    EqualsToPattern(final Object value) {
        this.value = value;
    }

    @Override
    public boolean match(final Object expr) {
        return Objects.equals(value, expr);
    }
}
