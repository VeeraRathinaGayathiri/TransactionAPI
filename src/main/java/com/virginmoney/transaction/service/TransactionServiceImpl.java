package com.virginmoney.transaction.service;


import com.virginmoney.transaction.dto.StatisticsDto;
import com.virginmoney.transaction.dto.TransactionDto;
import com.virginmoney.transaction.dto.TransactionRequestDto;
import com.virginmoney.transaction.dto.TransactionType;
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

    private final TransactionRepo transactionRepo;


  public TransactionServiceImpl(TransactionRepo transactionRepo) {
        this.transactionRepo = transactionRepo;
    }


    @Override
    public ResponseEntity<List<TransactionDto>> getLatestByCategory(String category) {

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
    public ResponseEntity<Double> getTotalSpendByCategory(String category) {
        List<TransactionEntity> allTransactions = fetchDataFromDb(category);

        double totalSpend = allTransactions.stream()
                    .map(TransactionEntity::getAmount)
                    .reduce(0.0, Double::sum);

        logger.debug("Response Status: {}, totalspendCalculated: {}",
                HttpStatus.OK, totalSpend > 0.0);

        return new ResponseEntity<>(totalSpend, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<Map<String, Double>> getMonthlyAverageByCategory(String category) {
        List<TransactionEntity> allTransactions = fetchDataFromDb(category);

        Map<String, Double> result = allTransactions.stream()
                    .collect(Collectors.groupingBy(
                            transaction -> transaction.getDate().toLocalDate().getMonth().name() + "_" + transaction.getDate().toLocalDate().getYear(),
                            Collectors.averagingDouble(TransactionEntity::getAmount)
                    ));

        logger.debug("Response Status: {}, NoOfmonthlyAveragesReturned: {}",
                HttpStatus.OK, result.size());

       return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<TransactionDto>> saveTransactions(List<TransactionRequestDto> transactionRequests) {

        //
        // TransactionEntity transactionEntity = transactionRepo.save(mapToEntity(transactionRequests));
        List<TransactionEntity> transactions = transactionRequests.stream()
                                                .map(this::mapToEntity)
                                                .collect(Collectors.toList());

        try{

            List<TransactionEntity> result = transactionRepo.saveAll(transactions);
            return new ResponseEntity<>(result.stream()
                    .map(this::mapToResponseDto)
                    .collect(Collectors.toList()),
                    HttpStatus.CREATED);
        }
        catch (Exception exception){
            logger.error("Error storing data - throws exception");
            throw new DatabaseFetchException("Error storing transactions to database");
        }


    }

    @Override
    public ResponseEntity<StatisticsDto> getYearlyStatisticsByCategory(String category, int year) {
        List<TransactionEntity> allTransactions = fetchDataFromDb(category);

        DoubleSummaryStatistics stats = allTransactions.stream()
                .filter(transaction -> transaction.getDate().toLocalDate().getYear() == year)
                .mapToDouble(TransactionEntity::getAmount)
                .summaryStatistics();

        if(stats.getCount() != 0) {
            StatisticsDto result = StatisticsDto.builder()
                    .lowest_spend(stats.getMin())
                    .highest_spend(stats.getMax())
                    .average_spend(Math.round(stats.getAverage()*100.0)/ 100.0)
                    .build();


            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        else {
            logger.error("No transaction found for this year category {} in {} - throws exception", category, year);
            throw new TransactionNotFound(String.format("No transactions found for the category: %s in %d", category, year));
        }
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
                .type(TransactionType.valueOf(transactionRequestDto.type().toUpperCase()))
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
                .type(String.valueOf(transactionEntity.getType()))
                .amount(transactionEntity.getAmount())
                .category(transactionEntity.getCategory())
                .build();
    }

}
