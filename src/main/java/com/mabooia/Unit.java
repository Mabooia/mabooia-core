package com.mabooia;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class Unit {

    public static final Unit SINGLETON = new Unit();

    public static SupplierEx<Unit> from(final Runnable runnable) {
        return () -> {
            runnable.run();
            return SINGLETON;
        };
    }

    public static <E extends Exception> ThrowingSupplier<Unit, E> from(final ThrowingRunnable<E> runnable) {
        return () -> {
            runnable.run();
            return SINGLETON;
        };
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(Unit.class)
            .build();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Unit;
    }

    @Override
    public String toString() {
        return "unit";
    }

    private Unit() {
    }
}
