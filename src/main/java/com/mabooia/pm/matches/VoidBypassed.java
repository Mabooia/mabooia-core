package com.mabooia.pm.matches;

import com.mabooia.pm.statements.CompletedStatement;
import com.mabooia.pm.statements.MatchStatement;
import java.util.function.Supplier;

public class VoidBypassed<R> implements VoidResult<R> {

    private final R result;

    public VoidBypassed(final R result) {
        this.result = result;
    }

    @Override
    public MatchStatement<R> then(final Supplier<R> f) {
        return new CompletedStatement<>(result);
    }
}
