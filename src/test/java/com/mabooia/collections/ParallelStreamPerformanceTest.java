package com.mabooia.collections;

import com.mabooia.collections.streams.Stream;
import com.mabooia.collections.streams.Streams;
import com.mabooia.concurrent.Tasks;
import com.mabooia.diagnostics.PerformanceUtils;
import org.junit.Test;

import java.time.Clock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.Assert.assertTrue;

public class ParallelStreamPerformanceTest {

    private final PerformanceUtils performanceUtils = new PerformanceUtils(Clock.systemDefaultZone());

    @Test
    public void test_allMatch_performance() {
        testPerformanceDifference(inParallel -> {
            // given
            final int count = 100;
            final Stream<Integer> naturals = Streams.intStartingAt(1);

            final Predicate<Integer> delayedPredicate = n -> {
                Tasks.sleep(1);
                return true;
            };

            final Runnable parallelOp = () ->
                naturals
                    .take(count)
                    .parallel()
                    .allMatch(delayedPredicate);

            final Runnable nonParallelOp = () ->
                naturals
                    .take(count)
                    .allMatch(delayedPredicate);

            return inParallel
                ? performanceUtils.elapsedMillisOf(parallelOp)
                : performanceUtils.elapsedMillisOf(nonParallelOp);
        });
    }

    @Test
    public void test_forEach_performance() {
        testPerformanceDifference(inParallel -> {
            // given
            final int count = 100;
            final Stream<Integer> naturals = Streams.intStartingAt(1);

            final Consumer<Integer> delayedConsumer = ignored -> Tasks.sleep(1);

            final Runnable parallelOp = () ->
                naturals
                    .take(count)
                    .parallel()
                    .forEach(delayedConsumer);

            final Runnable nonParallelOp = () ->
                naturals
                    .take(count)
                    .forEach(delayedConsumer);

            return inParallel
                ? performanceUtils.elapsedMillisOf(parallelOp)
                : performanceUtils.elapsedMillisOf(nonParallelOp);
        });
    }

    @Test
    public void test_reduce_performance() {
        testPerformanceDifference(inParallel -> {
            // given
            final int count = 100;
            final Stream<Integer> naturals = Streams.intStartingAt(1);

            final BiFunction<Long, Integer, Long> delayedSum = (acc, n) -> {
                Tasks.sleep(1);
                return acc + n;
            };

            final Runnable parallelOp = () -> naturals
                .take(count)
                .parallel()
                .reduce(0L, delayedSum, Long::sum);

            final Runnable nonParallelOp = () -> naturals
                .take(count)
                .reduce(0L, delayedSum);

            return inParallel
                ? performanceUtils.elapsedMillisOf(parallelOp)
                : performanceUtils.elapsedMillisOf(nonParallelOp);
        });
    }

    private void testPerformanceDifference(final Function<Boolean, Long> getElapsedTime) {
        final ExecutorService executorService = Executors.newFixedThreadPool(2);

        final Future<Long> nonParallelElapsedTimeFuture = executorService.submit(() -> getElapsedTime.apply(false));

        final long parallelElapsedTime = getElapsedTime.apply(true);
        final long nonParallelElapsedTime = Tasks.awaitGet(nonParallelElapsedTimeFuture);

        assertTrue(
            String.format(
                "Parallel operation should be faster. Parallel: %d ms, Non parallel: %d ms",
                parallelElapsedTime,
                nonParallelElapsedTime
            ),
            parallelElapsedTime < nonParallelElapsedTime
        );
    }
}
