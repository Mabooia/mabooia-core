package com.mabooia.pm;

import com.mabooia.pm.statements.ContinuingStatement;
import com.mabooia.pm.statements.MatchStatement;
import org.apache.commons.lang3.tuple.Pair;

public final class Match {

    public static <R> MatchStatement<R> when(final Object expr) {
        return new ContinuingStatement<>(expr);
    }

    public static <R> MatchStatement<R> when(final Object left, final Object right) {
        return when(Pair.of(left, right));
    }

    private Match() {
    }
}
