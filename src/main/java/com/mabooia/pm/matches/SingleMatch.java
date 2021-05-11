package com.mabooia.pm.matches;

import com.mabooia.pm.statements.CompletedStatement;
import com.mabooia.pm.statements.MatchStatement;
import java.util.function.Function;
import java.util.function.Predicate;

public class SingleMatch<EXPR, A, R> implements SingleResult<A, R> {

    private final EXPR expr;
    private final A arg;

    public SingleMatch(final EXPR expr, final A arg) {
        this.expr = expr;
        this.arg = arg;
    }

    @Override
    public MatchStatement<R> then(final Function<A, R> f) {
        final R res = f.apply(arg);
        return new CompletedStatement<>(res);
    }

    @Override
    public SingleResult<A, R> and(final Predicate<A> p) {
        if (p.test(arg)) {
            return this;
        }

        return new SingleMismatch<>(expr);
    }
}
