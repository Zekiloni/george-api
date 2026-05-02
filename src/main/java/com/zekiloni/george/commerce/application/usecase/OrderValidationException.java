package com.zekiloni.george.commerce.application.usecase;

public class OrderValidationException extends RuntimeException {
    public OrderValidationException(String message) {
        super(message);
    }
}
