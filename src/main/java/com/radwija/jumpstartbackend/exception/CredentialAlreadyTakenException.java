package com.radwija.jumpstartbackend.exception;

public class CredentialAlreadyTakenException extends RuntimeException {
    public CredentialAlreadyTakenException(String message) {
        super(message);
    }
}
