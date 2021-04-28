package com.mabooia;

/**
 * This exception is intended to be thrown in those points on the code that should never be reached
 */
public class ShouldNotReachThisPointException extends RuntimeException {

    public ShouldNotReachThisPointException() {
        super();
    }

    public ShouldNotReachThisPointException(final String message) {
        super(message);
    }

    public ShouldNotReachThisPointException(final Throwable cause) {
        super(cause);
    }

    public ShouldNotReachThisPointException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
