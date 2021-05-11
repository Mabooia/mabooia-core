package com.mabooia.pm.statements;

import com.mabooia.pm.NotMatchingPatternFoundException;
import com.mabooia.pm.matches.DoubleMatch;
import com.mabooia.pm.matches.DoubleMismatch;
import com.mabooia.pm.matches.DoubleResult;
import com.mabooia.pm.matches.SingleMatch;
import com.mabooia.pm.matches.SingleMismatch;
import com.mabooia.pm.matches.SingleResult;
import com.mabooia.pm.matches.VoidMatch;
import com.mabooia.pm.matches.VoidMismatch;
import com.mabooia.pm.matches.VoidResult;
import com.mabooia.pm.patterns.DoublePattern;
import com.mabooia.pm.patterns.Pattern;
import com.mabooia.pm.patterns.SinglePattern;
import java.util.Optional;
import java.util.function.Supplier;

public class ContinuingStatement<EXPR, R> implements MatchStatement<R> {

    private final EXPR expr;

    public ContinuingStatement(EXPR expr) {
        this.expr = expr;
    }

    @Override
    public R get() {
        throw new NotMatchingPatternFoundException();
    }

    @Override
    public Optional<R> toOptional() {
        return Optional.empty();
    }

    @Override
    public VoidResult<R> is(final Pattern pattern) {
        if (pattern.match(expr)) {
            return new VoidMatch<>();
        }

        return new VoidMismatch<>(expr);
    }

    @Override
    public <A> SingleResult<A, R> is(final SinglePattern<A> pattern) {
        if (pattern.match(expr)) {
            final A arg = pattern.getMatchedArg(expr);
            return new SingleMatch<>(expr, arg);
        }

        return new SingleMismatch<>(expr);
    }

    @Override
    public <A, B> DoubleResult<A, B, R> is(final DoublePattern<A, B> pattern) {
        if (pattern.match(expr)) {
            final A aArg = pattern.getFirstMatchedArg(expr);
            final B bArg = pattern.getSecondMatchedArg(expr);
            return new DoubleMatch<>(expr, aArg, bArg);
        }

        return new DoubleMismatch<>(expr);
    }

    @Override
    public R orElse(final Supplier<R> supplier) {
        return supplier.get();
    }
}
