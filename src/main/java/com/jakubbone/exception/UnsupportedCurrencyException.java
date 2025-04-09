package com.jakubbone.exception;

public class UnsupportedCurrencyException extends RuntimeException {
    public UnsupportedCurrencyException(String currency) {
        super("unknown currency: " + currency);
    }
}
