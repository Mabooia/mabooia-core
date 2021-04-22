package com.mabooia.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class Tasks {

    public static <A> A asyncCombine(final AtomicReference<A> ref,
                                     final A value,
                                     final BiFunction<A, A, A> combineFunc) {
        A acc;
        A combined;
        do {
            acc = ref.get();
            combined = combineFunc.apply(acc, value);
        } while (!ref.compareAndSet(acc, combined));
        return combined;
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        }
        catch (Exception ignored) { }
    }

    public static <B> Future<B> async(final ExecutorService executor, final Supplier<B> supplier) {
        return executor.submit(supplier::get);
    }

    public static void async(final ExecutorService executor, final Runnable runnable) {
        executor.submit(runnable);
    }

    public static void await(final Future<?> task) {
        awaitOrElse(task, null);
    }

    public static <A> A awaitGet(final Future<A> task) {
        return awaitOrElse(task, null);
    }

    public static <A> A awaitOrElse(final Future<A> task, final A orElseValue) {
        try {
            return task.get();
        }
        catch (Exception ignored) {
            return orElseValue;
        }
    }

    private Tasks() {}
}
