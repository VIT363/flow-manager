package com.vitzemtsov.flowmanager.exception.business;

public class InactiveSubscriptionException extends RuntimeException {

    public InactiveSubscriptionException(String message) {
        super(message);
    }
}