package com.radwija.jumpstartbackend.exception;

public class OutOfProductStockException extends RuntimeException {
    public OutOfProductStockException(String message) {
        super(message);
    }
}
