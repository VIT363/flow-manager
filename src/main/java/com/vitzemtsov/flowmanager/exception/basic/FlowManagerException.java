package com.vitzemtsov.flowmanager.exception.basic;

public abstract class FlowManagerException extends RuntimeException {

    protected FlowManagerException(String message) { super(message); }

    protected FlowManagerException(String message, Throwable cause) { super(message, cause); }
}
