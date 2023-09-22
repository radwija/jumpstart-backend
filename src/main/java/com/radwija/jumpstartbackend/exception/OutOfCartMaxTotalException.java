package com.radwija.jumpstartbackend.exception;

public class OutOfCartMaxTotalException extends RuntimeException {
    public OutOfCartMaxTotalException(String message) {
        super(message);
    }
}
