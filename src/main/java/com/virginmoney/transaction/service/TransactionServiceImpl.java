package com.virginmoney.transaction.service;


import com.virginmoney.transaction.dto.TransactionDto;
import com.virginmoney.transaction.dto.TransactionRequestDto;
import com.virginmoney.transaction.exception.DatabaseFetchException;
import com.virginmoney.transaction.exception.TransactionNotFound;
import com.virginmoney.transaction.model.TransactionEntity;
import com.virginmoney.transaction.repo.TransactionRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService{

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private TransactionRepo transactionRepo;

    public TransactionServiceImpl(TransactionRepo transactionRepo) {
        this.transactionRepo = transactionRepo;
    }


    @Override
    public ResponseEntity<List<TransactionDto>> getAllTransactions(String category) {

        List<TransactionEntity> allTransactions = fetchDataFromDb(category);

        List<TransactionDto> response = allTransactions.stream()
                                            .map(this::mapToResponseDto)
                                            .sorted(Comparator.comparing(TransactionDto::date))
                                            .toList();

        logger.debug("Response Status: {}, TransactionSize: {}",
               HttpStatus.OK, response.size());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Double> getTotalSpend(String category) {
        List<TransactionEntity> allTransactions = fetchDataFromDb(category);

        double totalSpend = allTransactions.stream()
                    .map(TransactionEntity::getAmount)
                    .reduce(0.0, Double::sum);
        logger.debug("Response Status: {}, totalspendCalculated: {}",
                HttpStatus.OK, totalSpend > 0.0);

        return new ResponseEntity<>(totalSpend, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<Map<String, Double>> getMonthlyAverage(String category) {
        List<TransactionEntity> allTransactions = fetchDataFromDb(category);

        Map<String, Double> result = allTransactions.stream()
                    .collect(Collectors.groupingBy(
                            x -> x.getDate().toLocalDate().getMonth().name() + "_" + x.getDate().toLocalDate().getYear(),
                            Collectors.averagingDouble(TransactionEntity::getAmount)
                    ));

        logger.debug("Response Status: {}, NoOfmonthlyAveragesReturned: {}",
                HttpStatus.OK, result.size());

       return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Double> getHighestSpend(String category, int year) {
        List<TransactionEntity> allTransactions = fetchDataFromDb(category);

       OptionalDouble result = allTransactions.stream()
                .filter(x -> x.getDate().toLocalDate().getYear() == year)
               .mapToDouble(TransactionEntity::getAmount)
               .max();

        if(result.isPresent()) {
        logger.debug("Response Status: {}, isHighestSpendCalculated: {}",
                HttpStatus.OK, result.getAsDouble());

        return(new ResponseEntity<>(result.getAsDouble(), HttpStatus.OK));
        }

        else {
            logger.error("No transaction found for category {} on {} - throws exception", category, year);
            throw new TransactionNotFound("No transaction found for this category and year combination");

        }
    }

    @Override
    public ResponseEntity<Double> getLowestSpend(String category, int year) {
        List<TransactionEntity> allTransactions = fetchDataFromDb(category);
        OptionalDouble result = allTransactions.stream()
                .filter(x -> x.getDate().toLocalDate().getYear() == year)
                .mapToDouble(TransactionEntity::getAmount)
                .min();

        if(result.isPresent()) {
            logger.debug("Response Status: {}, isLowestSpendCalculated: {}",
                    HttpStatus.OK, result.getAsDouble());

            return(new ResponseEntity<>(result.getAsDouble(), HttpStatus.OK));
        }
        else {
            logger.error("No transaction found for category {} on {} - throws exception", category, year);
            throw new TransactionNotFound("No transaction found for this category and year combination");
        }
    }

    @Override
    public ResponseEntity<TransactionDto> saveTransactions(TransactionRequestDto transactionRequestDto) {

        TransactionEntity transactionEntity = transactionRepo.save(mapToEntity(transactionRequestDto));

        return new ResponseEntity<>(mapToResponseDto(transactionEntity), HttpStatus.CREATED);
    }

     private List<TransactionEntity> fetchDataFromDb(String category) {

        logger.info("Fecthing data from DB invoked");

        List<TransactionEntity> allTransactions = new ArrayList<>();

        Optional<List<TransactionEntity>> transactions = Optional.of(new ArrayList<TransactionEntity>());

        try {
            transactions = transactionRepo.findByCategory(category);
            logger.debug("Data fetch - Successful");
        }
        catch (Exception exception){
            logger.error("Error fetching data - throws exception");
            throw new DatabaseFetchException("Error fetching transactions from database");
        }

        if (transactions.isPresent() && !transactions.get().isEmpty()) {
            transactions.get().forEach(allTransactions::add);
            return allTransactions;
        } else {
            logger.error("No transaction found category {} - throws exception", category);
            throw new TransactionNotFound("No transactions found for the category : " + category);
        }
    }

    private TransactionEntity mapToEntity(TransactionRequestDto transactionRequestDto) {

        return TransactionEntity.builder()
                .date(transactionRequestDto.date())
                .vendor(transactionRequestDto.vendor())
                .type(transactionRequestDto.type())
                .amount(transactionRequestDto.amount())
                .category(transactionRequestDto.category())
                .build();
    }

    private TransactionDto mapToResponseDto(TransactionEntity transactionEntity){

        logger.info("Entity to Dto Mapping invoked");
         return TransactionDto.builder()
                .id(transactionEntity.getId())
                .date(transactionEntity.getDate())
                .vendor(transactionEntity.getVendor())
                .type(transactionEntity.getType())
                .amount(transactionEntity.getAmount())
                .category(transactionEntity.getCategory())
                .build();
    }

}
