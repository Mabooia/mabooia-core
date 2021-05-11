package com.mabooia.pm.matches;

import com.mabooia.pm.statements.MatchStatement;
import java.util.function.Function;
import java.util.function.Predicate;

public interface SingleResult<A, R> {

    MatchStatement<R> then(final Function<A, R> f);

    SingleResult<A, R> and(final Predicate<A> p);
}
