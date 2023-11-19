package com.enigma.duitku.exception;

public class BeneficiaryException extends Exception{

    public BeneficiaryException() {
    }

    public BeneficiaryException(String message) {
        super(message);
    }

    public BeneficiaryException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeneficiaryException(Throwable cause) {
        super(cause);
    }

    public BeneficiaryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
