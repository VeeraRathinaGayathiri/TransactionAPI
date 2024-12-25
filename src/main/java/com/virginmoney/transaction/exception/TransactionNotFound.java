package com.virginmoney.transaction.exception;

public class TransactionNotFound extends RuntimeException {
    public TransactionNotFound(String message){
        super(message);
    }
}
