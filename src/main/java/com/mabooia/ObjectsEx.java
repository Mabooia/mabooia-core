package com.mabooia;

public final class ObjectsEx {

    public static boolean anyNull(final Object...objects) {
        for (final Object obj: objects) {
            if (obj == null) {
                return true;
            }
        }
        return false;
    }

    public static boolean allNonNull(final Object...objects) {
        for (final Object obj: objects) {
            if (obj == null) {
                return false;
            }
        }
        return true;
    }

    public static <T> boolean is(final Class<T> type, final Object obj) {
        return allNonNull(type, obj) && type.isAssignableFrom(obj.getClass());
    }

    public static <A> A as(final Class<A> type, final Object obj) {
        if (is(type, obj)) {
            return type.cast(obj);
        }
        return null;
    }

    private ObjectsEx() {
    }
}
