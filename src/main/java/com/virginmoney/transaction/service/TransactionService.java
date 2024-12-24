package com.virginmoney.transaction.service;

import com.virginmoney.transaction.dto.TransactionDto;
import com.virginmoney.transaction.dto.TransactionRequestDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface TransactionService {
    ResponseEntity<List<TransactionDto>> getAllTransactions(String category);

    ResponseEntity<Double> getTotalSpend(String category);

    ResponseEntity<Map<String, Double>> getMonthlyAverage(String category);

    ResponseEntity<Map<String, Double>> getHighestSpend(String category, int year);

    ResponseEntity<Map<String, Double>> getLowestSpend(String category, int year);

    ResponseEntity<TransactionDto> saveTransactions(TransactionRequestDto transactionRequestDto);
}
