package com.mabooia;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;
import org.junit.Test;

public class TryTest {

    public static <T> void assertSuccess(final Try<T> res) {
        assertTrue(res.isSuccess());
        assertFalse(res.isFailure());
        assertTrue(res.toOptional().isPresent());
        assertFalse(res.toFailureOptional().isPresent());
    }

    public static <T> void assertSuccess(final Try<T> res, T result) {
        assertSuccess(res);
        assertTrue(res.toOptional().isPresent());
        assertEquals(result, res.toOptional().get());
    }

    public static void assertFailure(final Try<?> res, final Exception ex) {
        assertFalse(res.isSuccess());
        assertTrue(res.isFailure());
        assertFalse(res.toOptional().isPresent());
        assertTrue(res.toFailureOptional().isPresent());
        assertEquals(ex, res.toFailureOptional().get());
    }

    @Test
    public void testSuccess() {
        // given
        final Object result = new Object();
        final Try<Object> res = Try.success(result);

        // then
        assertSuccess(res, result);
    }

    @Test
    public void testFailure() {
        // given
        final Exception ex = new Exception();
        final Try<Object> res = Try.failure(ex);

        // then
        assertFailure(res, ex);
    }

    @Test
    public void testSucceededTry() {
        // given
        final Object result = new Object();
        final Try<Object> res = Try.of(() -> result);

        // then
        assertSuccess(res, result);
    }

    @Test
    public void testFailedTry() {
        // given
        final Exception ex = new Exception();
        final Try<Object> res = Try.of(() -> {
            throw ex;
        });

        // then
        assertFailure(res, ex);
    }

    @Test
    public void testIfSuccessWhenIsSuccess() {
        // given
        final Try<Object> res = Try.success(new Object());

        @SuppressWarnings("unchecked")
        final Consumer<Object> consumer = mock(Consumer.class);
        doNothing().when(consumer).accept(any());

        // when
        final Try<Object> afterCall = res.ifSuccess(consumer);

        // then
        verify(consumer, times(1)).accept(any());
        assertEquals(res, afterCall);
    }

    @Test
    public void testIfSuccessWhenIsFailure() {
        // given
        final Try<Object> res = Try.failure(new Exception());

        @SuppressWarnings("unchecked")
        final Consumer<Object> consumer = mock(Consumer.class);
        doNothing().when(consumer).accept(any());

        // when
        final Try<Object> afterCall = res.ifSuccess(consumer);

        // then
        verify(consumer, never()).accept(any());
        assertEquals(res, afterCall);
    }

    @Test
    public void testIfFailureWhenIsFailure() {
        // given
        final Try<Object> res = Try.failure(new Exception());

        @SuppressWarnings("unchecked")
        final Consumer<Exception> consumer = mock(Consumer.class);
        doNothing().when(consumer).accept(any());

        // when
        final Try<Object> afterCall = res.ifFailure(consumer);

        // then
        verify(consumer, times(1)).accept(any());
        assertEquals(res, afterCall);
    }

    @Test
    public void testIfFailureWhenIsSuccess() {
        // given
        final Try<Object> res = Try.success(new Object());

        @SuppressWarnings("unchecked")
        final Consumer<Exception> consumer = mock(Consumer.class);
        doNothing().when(consumer).accept(any());

        // when
        final Try<Object> afterCall = res.ifFailure(consumer);

        // then
        verify(consumer, never()).accept(any());
        assertEquals(res, afterCall);
    }

    @Test
    public void testIfFailureWith() {
        // given
        final Try<Object> res = Try.failure(new IOException());

        @SuppressWarnings("unchecked")
        final Consumer<IOException> consumerOfIoException = mock(Consumer.class);
        doNothing().when(consumerOfIoException).accept(any(IOException.class));

        @SuppressWarnings("unchecked")
        final Consumer<NullPointerException> consumerOfNullException = mock(Consumer.class);
        doNothing().when(consumerOfNullException).accept(any(NullPointerException.class));

        // when
        final Try<Object> afterCall = res
            .ifFailureWith(NullPointerException.class, consumerOfNullException)
            .ifFailureWith(IOException.class, consumerOfIoException);

        // then
        verify(consumerOfNullException, never()).accept(any());
        verify(consumerOfIoException, times(1)).accept(any());
        assertEquals(res, afterCall);
    }

    @Test
    public void testRecoverIfFailureWith() throws Exception {
        // given
        final Try<Object> res = Try.failure(new IOException());
        final Object recoverFromNPE = new Object();
        final Object recoverFromIOE = new Object();

        @SuppressWarnings("unchecked")
        final ThrowingFunction<IOException, Object, Exception> recoverFromIOException =
            mock(ThrowingFunction.class);
        when(recoverFromIOException.apply(any(IOException.class))).thenReturn(recoverFromIOE);

        @SuppressWarnings("unchecked")
        final ThrowingFunction<NullPointerException, Object, Exception> recoverFromNullException =
            mock(ThrowingFunction.class);
        when(recoverFromNullException.apply(any(NullPointerException.class))).thenReturn(recoverFromNPE);

        // when
        final Try<Object> afterCall = res
            .recoverIfFailureWith(NullPointerException.class, recoverFromNullException)
            .recoverIfFailureWith(IOException.class, recoverFromIOException);

        // then
        verify(recoverFromNullException, never()).apply(any());
        verify(recoverFromIOException, times(1)).apply(any());
        assertEquals(recoverFromIOE, afterCall.toOptional().orElse(null));
    }

    @Test
    public void testFlatMapSuccess() {
        // given
        final Object r1 = new Object();
        final Object r2 = new Object();

        @SuppressWarnings("unchecked")
        final Function<Object, Try<Object>> f = mock(Function.class);
        when(f.apply(any())).thenReturn(Try.success(r2));

        // when
        final Try<Object> res2 = Try
            .success(r1)
            .flatMap(f);

        // then
        assertSuccess(res2, r2);
        verify(f, times(1)).apply(any());
    }

    @Test
    public void testFlatMapFailureBeforeMapping() {
        // given
        final Exception ex1 = new Exception();
        final Exception ex2 = new Exception();
        final Try<Object> res1 = Try.failure(ex1);

        @SuppressWarnings("unchecked")
        final Function<Object, Try<Object>> function = mock(Function.class);
        when(function.apply(any())).thenReturn(Try.failure(ex2));

        // when
        final Try<Object> res2 = res1.flatMap(function);

        // then
        assertFailure(res2, ex1);
        verify(function, never()).apply(any());
    }

    @Test
    public void testFlatMapFailureWhileMapping() {
        // given
        final Exception ex = new Exception();
        final Try<Object> res1 = Try.success(new Object());

        @SuppressWarnings("unchecked")
        final Function<Object, Try<Object>> function = mock(Function.class);
        when(function.apply(any())).thenReturn(Try.failure(ex));

        // when
        final Try<Object> res2 = res1.flatMap(function);

        // then
        assertFailure(res2, ex);
        verify(function, times(1)).apply(any());
    }

    @Test
    public void testFlattenWhenIsFullySuccess() {
        // given
        final Object obj = new Object();
        final Try<Try<Object>> res = Try.success(Try.success(obj));

        // when
        final Try<Object> flat = Try.flatten(res);

        // then
        assertSuccess(flat, obj);
    }

    @Test
    public void testFlattenWhenOnlyFirstLevelIsSuccess() {
        // given
        final Exception ex = new Exception();
        final Try<Try<Object>> res = Try.success(Try.failure(ex));

        // when
        final Try<Object> flat = Try.flatten(res);

        // then
        assertFailure(flat, ex);
    }

    @Test
    public void testFlattenWhenFirstLevelIsFailure() {
        // given
        final Exception ex = new Exception();
        final Try<Try<Object>> res = Try.failure(ex);

        // when
        final Try<Object> flat = Try.flatten(res);

        // then
        assertFailure(flat, ex);
    }

    @Test
    public void testMapSuccess() {
        // given
        final Object r1 = new Object();
        final Object r2 = new Object();
        final Try<Object> res1 = Try.success(r1);
        final ThrowingFunction<Object, Object, ?> f = ignored -> r2;

        // when
        final Try<Object> res2 = res1.map(f);

        // then
        assertSuccess(res2, r2);
    }

    @Test
    public void testMapFailureBeforeMapping() {
        // given
        final Exception ex1 = new Exception();
        final Try<Object> res1 = Try.failure(ex1);

        @SuppressWarnings("unchecked")
        final ThrowingFunction<Object, Object, NullPointerException> failingFunction = mock(ThrowingFunction.class);

        doThrow(new NullPointerException())
            .when(failingFunction)
            .apply(any());

        // when
        final Try<Object> res2 = res1.map(failingFunction);

        // then
        assertFailure(res2, ex1);
        verify(failingFunction, never()).apply(any());
    }

    @Test
    public void testMapFailureWhileMapping() {
        // given
        final Try<Object> res1 = Try.success(new Object());
        final NullPointerException ex = new NullPointerException();
        @SuppressWarnings("unchecked")
        final ThrowingFunction<Object, Object, NullPointerException> failingFunction = mock(ThrowingFunction.class);

        doThrow(ex)
            .when(failingFunction)
            .apply(any());

        // when
        final Try<Object> res2 = res1.map(failingFunction);

        // then
        assertFailure(res2, ex);
        verify(failingFunction, times(1)).apply(any());
    }

    @Test
    public void testThrowIfFailureWhenIsSuccess() throws Exception {
        // given
        final Try<Object> res = Try.success(new Object());

        // then
        assertEquals(res, res.throwIfFailure());
    }

    @Test
    public void testThrowIfFailureWhenIsFailure() {
        // given
        final Exception ex = new Exception();
        final Try<Object> res = Try.failure(ex);

        // then
        assertEquals(
            ex,
            assertThrows(Exception.class, res::throwIfFailure)
        );
    }

    @Test
    public void testThrowIfFailureWithWhenIsFailure() {
        // given
        final IOException ex = new IOException();
        final Try<Object> res = Try.failure(ex);

        // then
        assertEquals(
            res,
            res.throwIfFailureWith(NullPointerException.class)
        );

        assertEquals(
            ex,
            assertThrows(Exception.class, () -> res.throwIfFailureWith(IOException.class))
        );

        assertEquals(
            ex,
            assertThrows(Exception.class, () -> res.throwIfFailureWith(Exception.class))
        );
    }
}
