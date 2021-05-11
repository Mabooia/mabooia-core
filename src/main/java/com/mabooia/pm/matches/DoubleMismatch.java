package com.mabooia.pm.matches;

import com.mabooia.pm.statements.ContinuingStatement;
import com.mabooia.pm.statements.MatchStatement;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class DoubleMismatch<EXPR, A, B, R> implements DoubleResult<A, B, R> {

    private final EXPR expr;

    public DoubleMismatch(final EXPR expr) {
        this.expr = expr;
    }

    @Override
    public MatchStatement<R> then(final BiFunction<A, B, R> f) {
        return new ContinuingStatement<>(expr);
    }

    @Override
    public DoubleResult<A, B, R> and(BiPredicate<A, B> p) {
        return this;
    }
}
