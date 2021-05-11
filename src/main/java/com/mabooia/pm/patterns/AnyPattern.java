package com.mabooia.pm.patterns;

public class AnyPattern implements Pattern {

    @Override
    public boolean match(Object expr) {
        return true;
    }
}
