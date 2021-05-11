package com.mabooia.pm.matches;

import com.mabooia.pm.statements.CompletedStatement;
import com.mabooia.pm.statements.MatchStatement;
import java.util.function.Function;
import java.util.function.Predicate;

public class SingleBypassed<A, R> implements SingleResult<A, R> {

    private final R result;

    public SingleBypassed(final R result) {
        this.result = result;
    }

    @Override
    public MatchStatement<R> then(final Function<A, R> f) {
        return new CompletedStatement<>(result);
    }

    @Override
    public SingleResult<A, R> and(final Predicate<A> p) {
        return this;
    }
}
