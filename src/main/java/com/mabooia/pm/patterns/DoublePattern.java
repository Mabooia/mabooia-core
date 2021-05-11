package com.mabooia.pm.patterns;

public interface DoublePattern<A, B> {

    boolean match(final Object expr);

    A getFirstMatchedArg(final Object expr);

    B getSecondMatchedArg(final Object expr);
}
