package com.rzpgatewayprac.rzpgatewayintprac.exception;

public class SignatureVerificationException extends Exception {

    // Constructor that accepts a custom message
    public SignatureVerificationException(String message) {
        super(message);
    }

    // Constructor that accepts a custom message and a cause
    public SignatureVerificationException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts only a cause
    public SignatureVerificationException(Throwable cause) {
        super(cause);
    }
}
