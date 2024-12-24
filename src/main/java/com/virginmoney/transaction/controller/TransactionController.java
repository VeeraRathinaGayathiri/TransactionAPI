package com.virginmoney.transaction.controller;


import com.virginmoney.transaction.dto.TransactionDto;
import com.virginmoney.transaction.dto.TransactionRequestDto;
import com.virginmoney.transaction.service.TransactionService;
import com.virginmoney.transaction.service.TransactionServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    //@Autowired
    public TransactionController(TransactionServiceImpl transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping()
    public ResponseEntity<TransactionDto> saveTransactions(@RequestBody TransactionRequestDto transactionRequestDto) throws Exception {
        return transactionService.saveTransactions(transactionRequestDto);
    }

    @GetMapping("/{category}")
    public ResponseEntity<List<TransactionDto>> getAllTransactions(@PathVariable() String category){
        return transactionService.getAllTransactions(category);
    }

    @GetMapping("/totalspend/{category}")
    public ResponseEntity<Double> getTotalSpend(@PathVariable() String category){
        return transactionService.getTotalSpend(category);
    }

    @GetMapping("/monthlyAverage/{category}")
    public ResponseEntity<Map<String, Double>> getMonthlyAverage(@PathVariable() String category){
       return transactionService.getMonthlyAverage(category);
    }

    @GetMapping("/highestSpend/{category}")
    public ResponseEntity<Map<String, Double>> getHighestSpend(@PathVariable() String category, @RequestParam() int year) {
        return transactionService.getHighestSpend(category, year);
    }

    @GetMapping("lowestSpend/{category}")
    public ResponseEntity<Map<String, Double>> getLowestSpend(@PathVariable() String category, @RequestParam() int year){
        return transactionService.getLowestSpend(category, year);
    }

}
