package com.shivendra.inventory_api.exception;

public class BusinessLogicException extends RuntimeException {

    private final String errorCode;

    public BusinessLogicException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessLogicException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}