package com.mabooia.pm.statements;

import com.mabooia.pm.matches.DoubleBypassed;
import com.mabooia.pm.matches.DoubleResult;
import com.mabooia.pm.matches.SingleBypassed;
import com.mabooia.pm.matches.SingleResult;
import com.mabooia.pm.matches.VoidBypassed;
import com.mabooia.pm.matches.VoidResult;
import com.mabooia.pm.patterns.DoublePattern;
import com.mabooia.pm.patterns.Pattern;
import com.mabooia.pm.patterns.SinglePattern;
import java.util.Optional;
import java.util.function.Supplier;

public class CompletedStatement<R> implements MatchStatement<R> {

    private final R result;

    public CompletedStatement(final R result) {
        this.result = result;
    }

    @Override
    public R get() {
        return result;
    }

    @Override
    public Optional<R> toOptional() {
        return Optional.ofNullable(result);
    }

    @Override
    public VoidResult<R> is(final Pattern pattern) {
        return new VoidBypassed<>(result);
    }

    @Override
    public <A> SingleResult<A, R> is(final SinglePattern<A> pattern) {
        return new SingleBypassed<>(result);
    }

    @Override
    public <A, B> DoubleResult<A, B, R> is(final DoublePattern<A, B> pattern) {
        return new DoubleBypassed<>(result);
    }

    @Override
    public R orElse(final Supplier<R> supplier) {
        return result;
    }
}
