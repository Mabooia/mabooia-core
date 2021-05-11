package com.mabooia.pm.matches;

import com.mabooia.pm.statements.CompletedStatement;
import com.mabooia.pm.statements.MatchStatement;
import java.util.function.Supplier;

public class VoidMatch<R> implements VoidResult<R> {

    @Override
    public MatchStatement<R> then(final Supplier<R> f) {
        final R res = f.get();
        return new CompletedStatement<>(res);
    }
}
