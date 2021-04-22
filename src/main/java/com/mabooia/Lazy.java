package com.mabooia;

import com.mabooia.concurrent.Tasks;

import java.util.Optional;
import java.util.concurrent.Future;
import java.util.function.Supplier;

/**
 *  This type represents a lazy value of type T.
 *
 * @param <T> The type of the inner lazy value
 */
public interface Lazy<T> {

    /**
     * Computes the lazy value in case it's not yet computed and returns it.
     *
     * @return the lazy value
     */
    T get();

    /**
     * Returns {@code true} if lazy value is already computed, otherwise returns {@code false}.
     *
     * @return whether the value is already computed or not
     */
    boolean isComputed();

    /**
     * Returns an Optional with the lazy value if it's already computed, otherwise returns empty.
     *
     * @return an Optional with the lazy value if it's computed
     */
    default Optional<T> getIfComputed() {
        return isComputed() ? Optional.of(get()) : Optional.empty();
    }

    /**
     * Returns an already computed lazy object with value as inner value.
     * The passed value cannot be {@code null}.
     *
     * @param value the computed value
     * @param <T>   the type of the inner value
     * @return      an already computed value
     */
    static <T> Lazy<T> of(final T value) {
        return new ComputedLazy<>(value);
    }

    static <T> Lazy<T> of(final Supplier<T> supplier) {
        return new SynchronizedLazy<>(supplier);
    }

    static <T> Lazy<T> of(final Future<T> future) {
        return new FutureLazy<>(future);
    }

    static <T> Lazy<T> notSyncOf(final Supplier<T> supplier) {
        return new NotSynchronizedLazy<>(supplier);
    }

    static <T> Lazy<T> publicationOf(final Supplier<T> supplier) {
        return new PublishedLazy<>(supplier);
    }
}

final class ComputedLazy<T> implements Lazy<T> {

    private final T value;

    ComputedLazy(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public boolean isComputed() {
        return true;
    }

    @Override
    public Optional<T> getIfComputed() {
        return Optional.of(value);
    }
}

abstract class NotComputedLazyBase<T> implements Lazy<T> {

    protected T value;
    protected boolean computed;
    protected final Supplier<T> supplier;

    @Override
    public T get() {
        computeIfNeeded();
        return value;
    }

    @Override
    public boolean isComputed() {
        return computed;
    }

    @Override
    public Optional<T> getIfComputed() {
        return computed ? Optional.of(value) : Optional.empty();
    }

    protected NotComputedLazyBase(Supplier<T> supplier) {
        value = null;
        computed = false;
        this.supplier = supplier;
    }

    protected abstract void computeIfNeeded();
}

final class NotSynchronizedLazy<T> extends NotComputedLazyBase<T> {

    NotSynchronizedLazy(Supplier<T> supplier) {
        super(supplier);
    }

    @Override
    protected void computeIfNeeded() {
        if (!computed) {
            value = supplier.get();
            computed = true;
        }
    }
}

final class SynchronizedLazy<T> extends NotComputedLazyBase<T> {

    private final Object syncObj = new Object();

    SynchronizedLazy(Supplier<T> supplier) {
        super(supplier);
    }

    @Override
    protected void computeIfNeeded() {
        if (!computed) {
            synchronized (syncObj) {
                if (!computed) {
                    value = supplier.get();
                    computed = true;
                }
            }
        }
    }
}

final class PublishedLazy<T> extends NotComputedLazyBase<T> {

    private final Object syncObj = new Object();

    PublishedLazy(Supplier<T> supplier) {
        super(supplier);
    }

    @Override
    protected void computeIfNeeded() {
        if (!computed) {
            final T computedValue = supplier.get();
            synchronized (syncObj) {
                if (!computed) {
                    value = computedValue;
                    computed = true;
                }
            }
        }
    }
}

final class FutureLazy<T> implements Lazy<T> {

    private final Future<T> future;

    FutureLazy(final Future<T> future) {
        this.future = future;
    }

    @Override
    public T get() {
        return Tasks.awaitGet(future);
    }

    @Override
    public boolean isComputed() {
        return future.isDone();
    }
}
