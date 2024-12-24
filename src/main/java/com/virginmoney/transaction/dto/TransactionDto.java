package com.virginmoney.transaction.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.sql.Date;

@Builder
public record TransactionDto(long id,
                             @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MMM/yyyy") Date date,
                             String vendor,
                             String type,
                             double amount,
                             String category) {

}
