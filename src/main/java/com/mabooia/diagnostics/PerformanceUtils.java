package com.mabooia.diagnostics;

import org.apache.commons.lang3.tuple.Pair;

import java.time.Clock;
import java.util.function.Supplier;

public final class PerformanceUtils {

    private final Clock clock;

    public PerformanceUtils(final Clock clock) {
        this.clock = clock;
    }

    public <A> Pair<Long, A> elapsedMillisOf(final Supplier<A> supplier) {
        final long startTime = clock.millis();
        final A res = supplier.get();
        final long elapsedTime = clock.millis() - startTime;

        return Pair.of(elapsedTime, res);
    }

    public long elapsedMillisOf(final Runnable runnable) {
        final long startTime = clock.millis();
        runnable.run();
        return clock.millis() - startTime;
    }
}
