package com.virginmoney.transaction.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Date;

public record TransactionRequestDto(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MMM/yyyy") Date date, String vendor, String type, double amount, String category) {
}
