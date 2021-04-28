package com.mabooia;

import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public abstract class Try<T> {

    public static <T> Try<T> success(final T value) {
        return Success.of(value);
    }

    public static <T> Try<T> failure(final Exception ex) {
        return Failure.of(ex);
    }

    public static <RES extends Closeable, E extends IOException> Try<Unit> of(
        final ThrowingSupplier<RES, E> resourceSupplier,
        final ThrowingConsumer<RES, E> resourceConsumer) {

        return Try.of(() -> {
            try(final RES resource = resourceSupplier.get()) {
                resourceConsumer.accept(resource);
            }
        });
    }

    public static <R, RES extends Closeable, E extends IOException> Try<R> of(
        final ThrowingSupplier<RES, E> resourceSupplier,
        final ThrowingFunction<RES, R, E> resourceConsumer) {

        return Try.of(() -> {
            try(final RES resource = resourceSupplier.get()) {
                return resourceConsumer.apply(resource);
            }
        });
    }

    public static Try<Unit> of(final ThrowingRunnable<? extends Exception> runnable) {
        return of(Unit.from(runnable));
    }

    public static <T> Try<T> of(final ThrowingSupplier<T, ? extends  Exception> supplier) {
        try {
            final T result = supplier.get();
            return success(result);
        }
        catch (final Exception ex) {
            return failure(ex);
        }
    }

    public static <T> Try<T> flatten(final Try<Try<T>> overTry) {
        return overTry
            .toOptional()
            .orElseGet(() ->
                overTry
                    .toFailureOptional()
                    .<Try<T>>map(Try::failure)
                    .orElseGet(() ->failure(new ShouldNotReachThisPointException()))
            );
    }

    public abstract Optional<T> toOptional();

    public abstract Optional<Exception> toFailureOptional();

    public abstract boolean isSuccess();

    public boolean isFailure() {
        return !isSuccess();
    }

    public Try<T> ifSuccess(final Consumer<T> consumer) {
        toOptional().ifPresent(consumer);
        return this;
    }

    public Try<T> ifFailure(final Consumer<Exception> consumer) {
        toFailureOptional().ifPresent(consumer);
        return this;
    }

    public Try<T> throwIfFailure() throws Exception {
        final Exception ex = toFailureOptional().orElse(null);
        if (ex != null) {
            throw ex;
        }

        return this;
    }

    public <E extends Exception> Try<T> throwIfFailureWith(final Class<E> exClass) throws E {
        final Exception ex = toFailureOptional().orElse(null);
        if (ex != null && exClass.isAssignableFrom(ex.getClass())) {
            throw exClass.cast(ex);
        }

        return this;
    }

    public <E extends Exception> Try<T> ifFailureWith(
        final Class<E> exClass,
        final Consumer<E> catchConsumer) {

        toFailureOptional()
            .filter(ex -> exClass.isAssignableFrom(ex.getClass()))
            .map(exClass::cast)
            .ifPresent(catchConsumer);

        return this;
    }

    public <E extends Exception, E1 extends Exception> Try<T> recoverIfFailureWith(
        final Class<E> exClass,
        final ThrowingFunction<E, T, E1> recoverFunc) {

        return toFailureOptional()
            .filter(ex -> exClass.isAssignableFrom(ex.getClass()))
            .map(exClass::cast)
            .map(ex -> Try.of(() -> recoverFunc.apply(ex)))
            .orElse(this);
    }

    public <R> Try<R> flatMap(final Function<T, Try<R>> f) {
        return toOptional()
            .map(f)
            .orElseGet(() -> Failure.transform(this));
    }

    public <R> Try<R> map(final ThrowingFunction<T, R, ? extends Exception> f) {
        return toOptional()
            .map(result -> Try.of(() -> f.apply(result)))
            .orElseGet(() ->
                failure(
                    toFailureOptional().orElse(new Exception("Unknown error"))
                )
            );
    }
}

final class Success<T> extends Try<T> {

    public static <T> Try<T> of(final T value) {
        return new Success<>(value);
    }

    private final T result;

    private Success(T result) {
        this.result = result;
    }

    @Override
    public Optional<T> toOptional() {
        return Optional.ofNullable(result);
    }

    @Override
    public Optional<Exception> toFailureOptional() {
        return Optional.empty();
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(Success.class)
            .append(result)
            .build();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Success
            && new EqualsBuilder()
            .append(result, ((Success<?>)obj).result)
            .build();
    }

    @Override
    public String toString() {
        return String.format("Success(%s)", toOptional().map(Objects::toString).orElse("NULL"));
    }
}

final class Failure<T> extends Try<T> {

    public static <T> Try<T> of(final Exception ex) {
        return new Failure<>(ex);
    }

    public static <A, B> Try<B> transform(final Try<A> res) {
        return res
            .toFailureOptional()
            .<Try<B>>map(Try::failure)
            .orElseGet(() -> of(new ShouldNotReachThisPointException()));
    }

    private final Exception exception;

    private Failure(Exception exception) {
        this.exception = exception;
    }

    @Override
    public Optional<T> toOptional() {
        return Optional.empty();
    }

    @Override
    public Optional<Exception> toFailureOptional() {
        return Optional.of(exception);
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(Failure.class)
            .append(exception)
            .build();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Failure
            && new EqualsBuilder()
            .append(exception, ((Failure<?>)obj).exception)
            .build();
    }

    @Override
    public String toString() {
        return String.format("Failure(%s)", toFailureOptional().map(Objects::toString).orElse("NULL"));
    }
}
