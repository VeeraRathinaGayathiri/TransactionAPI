package com.virginmoney.transaction.model;

public record ErrorResponse(int status, String message, long timestamp) {
}
