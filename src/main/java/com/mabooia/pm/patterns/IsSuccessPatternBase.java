package com.mabooia.pm.patterns;

import com.mabooia.ObjectsEx;
import com.mabooia.Try;
import java.util.Optional;

public abstract class IsSuccessPatternBase {

    public boolean match(final Object expr) {
        return Optional
            .<Try<?>>ofNullable(ObjectsEx.as(Try.class, expr))
            .flatMap(Try::toOptional)
            .filter(this::patternMatch)
            .isPresent();
    }

    protected abstract boolean patternMatch(final Object expr);
}
