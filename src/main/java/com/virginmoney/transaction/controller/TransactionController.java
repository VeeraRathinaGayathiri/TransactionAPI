package com.virginmoney.transaction.controller;


import com.virginmoney.transaction.dto.TransactionDto;
import com.virginmoney.transaction.dto.TransactionRequestDto;
import com.virginmoney.transaction.service.TransactionServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private TransactionServiceImpl transactionService;

    //@Autowired
    public TransactionController(TransactionServiceImpl transactionService) {
        this.transactionService = transactionService;
    }

   @PostMapping()
    public ResponseEntity<TransactionDto> saveTransactions(@RequestBody TransactionRequestDto transactionRequestDto) {
        logger.info("Request Method: POST, URI: /transaction, Params: {}", transactionRequestDto.date()+ transactionRequestDto.vendor());
        return transactionService.saveTransactions(transactionRequestDto);
    }

    @GetMapping("/{category}")
    public ResponseEntity<List<TransactionDto>> getAllTransactions(@PathVariable() String category){
        logger.info("Request Method: GET, URI: /transaction/category, Params: {}", category);
        return transactionService.getAllTransactions(category);
    }

    @GetMapping("/totalspend/{category}")
    public ResponseEntity<Double> getTotalSpend(@PathVariable() String category){
        logger.info("Request Method: GET, URI: /transaction/totalspend/category, Params: {}", category);
        return transactionService.getTotalSpend(category);
    }

    @GetMapping("/monthlyAverage/{category}")
    public ResponseEntity<Map<String, Double>> getMonthlyAverage(@PathVariable() String category){
        logger.info("Request Method: GET, URI: /transaction/monthlyAverage/category, Params: {}", category);
       return transactionService.getMonthlyAverage(category);
    }

    @GetMapping("/highestSpend/{category}")
    public ResponseEntity<Double> getHighestSpend(@PathVariable() String category, @RequestParam() int year) {
        logger.info("Request Method: GET, URI: /transaction/highestSpend/category, Params: {} - {}", category , year);
        return transactionService.getHighestSpend(category, year);
    }

    @GetMapping("lowestSpend/{category}")
    public ResponseEntity<Double> getLowestSpend(@PathVariable() String category, @RequestParam() int year){
        logger.info("Request Method: GET, URI: /transaction/lowestSpend/category, Params: {} - {}", category, year);
        return transactionService.getLowestSpend(category, year);
    }

}
