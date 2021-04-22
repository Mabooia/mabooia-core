package com.mabooia;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LazyTest {

    @Test
    public void testGet() {
        testAllLazyVariants(lazyBuilder -> {
            // given
            final Object expectedValue = new Object();

            // when
            final Lazy<?> lazy = lazyBuilder.apply(() -> expectedValue);

            // then
            assertEquals(expectedValue, lazy.get());
        });
    }

    @Test
    public void testIsComputed() {
        testAllLazyVariants(lazyBuilder -> {
            // given
            final Lazy<?> lazy = lazyBuilder.apply(Object::new);

            // when
            lazy.get();

            // then
            assertTrue(lazy.isComputed());
        });
    }

    @Test
    public void testIsNotComputed() {
        testAllLazyVariants(lazyBuilder -> {
            // given
            final Lazy<?> lazy = lazyBuilder.apply(Object::new);

            // then
            assertFalse(lazy.isComputed());
        });
    }

    @Test
    public void testGetIfComputed() {
        testAllLazyVariants(lazyBuilder -> {
            // given
            final Object expectedValue = new Object();
            final Lazy<?> lazy = lazyBuilder.apply(() -> expectedValue);

            // when
            lazy.get();
            final Optional<?> optional = lazy.getIfComputed();

            // then
            assertTrue(optional.isPresent());
            assertSame(expectedValue, optional.get());
        });
    }

    @Test
    public void testGetIfNotComputed() {
        testAllLazyVariants(lazyBuilder -> {
            // given
            final Lazy<?> lazy = lazyBuilder.apply(Object::new);

            // when
            final Optional<?> optional = lazy.getIfComputed();

            // then
            assertFalse(optional.isPresent());
        });
    }

    @Test
    public void testCallsToSupplierBeforeBeingComputed() {
        testAllLazyVariants(lazyBuilder -> {
            // given
            @SuppressWarnings("unchecked") final Supplier<Object> supplierMock = mock(Supplier.class);
            when(supplierMock.get()).thenReturn(new Object());

            // when
            lazyBuilder.apply(supplierMock).getIfComputed();

            // then
            verify(supplierMock, never()).get();
        });
    }

    @Test
    public void testCallsToSupplierAfterBeingComputed() {
        testAllLazyVariants(lazyBuilder -> {
            // given
            @SuppressWarnings("unchecked") final Supplier<Object> supplierMock = mock(Supplier.class);
            when(supplierMock.get()).thenReturn(new Object());
            final Lazy<?> lazy = lazyBuilder.apply(supplierMock);

            // when
            lazy.get();
            lazy.get();
            lazy.getIfComputed();

            // then
            verify(supplierMock, times(1)).get();
        });
    }

    private <T> void testAllLazyVariants(final Consumer<Function<Supplier<T>, Lazy<T>>> consumer) {
        final List<Function<Supplier<T>, Lazy<T>>> lazyBuilders = Arrays.asList(
            Lazy::notSyncOf,
            Lazy::publicationOf,
            Lazy::of
        );

        lazyBuilders.forEach(consumer);
    }
}
