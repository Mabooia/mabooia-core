package com.mabooia.pm.matches;

import com.mabooia.pm.statements.MatchStatement;
import java.util.function.Supplier;

public interface VoidResult<R> {

    MatchStatement<R> then(final Supplier<R> f);
}
