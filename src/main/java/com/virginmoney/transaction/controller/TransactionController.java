package com.virginmoney.transaction.controller;


import com.virginmoney.transaction.dto.StatisticsDto;
import com.virginmoney.transaction.dto.TransactionDto;
import com.virginmoney.transaction.dto.TransactionRequestDto;
import com.virginmoney.transaction.service.TransactionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionServiceImpl transactionService;

   @PostMapping("/save")
    public ResponseEntity<List<TransactionDto>> saveTransactions(@RequestBody List<TransactionRequestDto> transactionRequests) {
        logger.info("Request Method: POST, URI: /transaction, Params : TransactionList of Size {}", transactionRequests.size());
        return transactionService.saveTransactions(transactionRequests);
    }

    @GetMapping("/{category}")
    public ResponseEntity<List<TransactionDto>> getLatestByCategory(@PathVariable() String category){
        logger.info("Request Method: GET, URI: /transaction/category, Params: {}", category);
        return transactionService.getLatestByCategory(category);
    }

    @GetMapping("/totalspend/{category}")
    public ResponseEntity<Double> getTotalSpendByCategory(@PathVariable() String category){
        logger.info("Request Method: GET, URI: /transaction/totalspend/category, Params: {}", category);
        return transactionService.getTotalSpendByCategory(category);
    }

    @GetMapping("/yearlyStatistics/{category}")
    public ResponseEntity<StatisticsDto> getYearlyStatisticsByCategory(@PathVariable() String category, @RequestParam() int year){
        logger.info("Request Method: GET, URI: /transaction/monthlyAverage/category, Params: {}", category, year);
        return transactionService.getYearlyStatisticsByCategory(category, year);
    }

    @GetMapping("/monthlyAverage/{category}")
    public ResponseEntity<Map<String, Double>> getMonthlyAverageByCategory(@PathVariable() String category){
        logger.info("Request Method: GET, URI: /transaction/monthlyAverage/category, Params: {}", category);
       return transactionService.getMonthlyAverageByCategory(category);
    }



}
