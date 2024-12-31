package com.virginmoney.transaction.service;

import com.virginmoney.transaction.dto.StatisticsDto;
import com.virginmoney.transaction.dto.TransactionDto;
import com.virginmoney.transaction.dto.TransactionRequestDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface TransactionService {
    ResponseEntity<List<TransactionDto>> getLatestByCategory(String category);

    ResponseEntity<Double> getTotalSpendByCategory(String category);

    ResponseEntity<Map<String, Double>> getMonthlyAverageByCategory(String category);

    ResponseEntity<List<TransactionDto>> saveTransactions(List<TransactionRequestDto> transactionRequests);

    ResponseEntity<StatisticsDto> getYearlyStatisticsByCategory(String category, int year);
}
