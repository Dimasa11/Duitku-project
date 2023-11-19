package com.enigma.duitku.exception;

public class TargetUserNotFoundException extends Exception{

    public TargetUserNotFoundException() {
    }

    public TargetUserNotFoundException(String message) {
        super(message);
    }

    public TargetUserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TargetUserNotFoundException(Throwable cause) {
        super(cause);
    }

    public TargetUserNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
