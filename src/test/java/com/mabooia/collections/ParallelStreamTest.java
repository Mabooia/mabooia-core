package com.mabooia.collections;

import com.mabooia.collections.streams.Stream;
import com.mabooia.collections.streams.Streams;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ParallelStreamTest {

    @Test
    public void test_parallel_allMatch() {
        // given
        final int count = 100;
        final Stream<Integer> naturals = Streams.intStartingAt(1);

        final AtomicInteger times = new AtomicInteger(0);
        final Predicate<Integer> delayedPredicate = n -> {
            times.incrementAndGet();
            return n < count / 2;
        };

        // when
        final boolean allMatches = naturals
            .take(count)
            .parallel()
            .allMatch(delayedPredicate);

        // then
        assertFalse(allMatches);
        assertTrue(count > times.get());
    }

    @Test
    public void test_parallel_forEach() {
        // given
        final int count = 1000;
        final Stream<Integer> naturals = Streams.intStartingAt(1);

        final AtomicInteger times = new AtomicInteger(0);
        final Consumer<Integer> delayedConsumer = ignored -> times.incrementAndGet();

        // when
        naturals
            .take(count)
            .parallel()
            .forEach(delayedConsumer);

        // then
        assertEquals(count, times.get());
    }

    @Test
    public void test_parallel_reduce() {
        // given
        final int count = 1000;
        final long expected = 500500L;

        final Stream<Integer> naturals = Streams.intStartingAt(1);

        // when
        final long sum = naturals
            .take(count)
            .parallel()
            .reduce(0L, Long::sum, Long::sum);

        // then
        assertEquals(expected, sum);
    }
}
