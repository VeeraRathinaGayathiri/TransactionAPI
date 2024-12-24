package com.virginmoney.transaction.service;


import com.virginmoney.transaction.dto.TransactionDto;
import com.virginmoney.transaction.dto.TransactionRequestDto;
import com.virginmoney.transaction.model.TransactionEntity;
import com.virginmoney.transaction.repo.TransactionRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService{

    private final TransactionRepo transactionRepo;

    public TransactionServiceImpl(TransactionRepo transactionRepo) {
        this.transactionRepo = transactionRepo;
    }


    @Override
    public ResponseEntity<List<TransactionDto>> getAllTransactions(String category) {
        List<TransactionEntity> allTransactions = new ArrayList<>();
        List<TransactionDto> resposne = new ArrayList<>();

        Optional<List<TransactionEntity>> transactions = transactionRepo.findByCategory(category);
        if(transactions.isPresent()){
            transactions.get().forEach(allTransactions::add);
            resposne = allTransactions.stream()
                    .map(this::mapToResponseDto)
                    .sorted(Comparator.comparing(TransactionDto::date))
                    .toList();
            return new ResponseEntity<>(resposne, HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(resposne, HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<Double> getTotalSpend(String category) {
        List<TransactionEntity> allTransactions = new ArrayList<>();
        double totalSpend = 0.0;

        Optional<List<TransactionEntity>> transactions = transactionRepo.findByCategory(category);
        if(transactions.isPresent()){
            transactions.get().forEach(allTransactions::add);
            totalSpend = allTransactions.stream()
                    .map(transactionEntity -> transactionEntity.getAmount())
                    .reduce(0.0, Double::sum);
        }
        return new ResponseEntity<>(totalSpend, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<Map<String, Double>> getMonthlyAverage(String category) {
        List<TransactionEntity> allTransactions = new ArrayList<>();
        Map<String, Double> result = new HashMap<>();

        Optional<List<TransactionEntity>> transactions = transactionRepo.findByCategory(category);
        if(transactions.isPresent()) {
            transactions.get().forEach(allTransactions::add);

            result = allTransactions.stream()
                    .collect(Collectors.groupingBy(
                            x -> x.getDate().toLocalDate().getMonth().name() + "_" + x.getDate().toLocalDate().getYear(),
                            Collectors.averagingDouble(TransactionEntity::getAmount)
                    ));

        }
       return new ResponseEntity<>(result, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<Map<String, Double>> getHighestSpend(String category, int year) {
        List<TransactionEntity> allTransactions = new ArrayList<>();
        Map<String, Double> result = new HashMap<>();

        Optional<List<TransactionEntity>> transactions = transactionRepo.findByCategory(category);
        if(transactions.isPresent()) {
            transactions.get().forEach(allTransactions::add);
            result = allTransactions.stream()
                    .filter(x -> x.getDate().toLocalDate().getYear() == year)
                    .collect(Collectors.groupingBy(
                            x -> x.getDate().toLocalDate().getMonth().name() + "_" + x.getDate().toLocalDate().getYear(),
                            Collectors.collectingAndThen(
                                    Collectors.maxBy(Comparator.comparing(TransactionEntity::getAmount)),
                                    optional -> optional.map(TransactionEntity::getAmount).orElse(0.0)
                            )

                    ));
        }
        return(new ResponseEntity<>(result, HttpStatus.OK));
    }

    @Override
    public ResponseEntity<Map<String, Double>> getLowestSpend(String category, int year) {
        List<TransactionEntity> allTransactions = new ArrayList<>();
        Map<String, Double> result = new HashMap<>();

        Optional<List<TransactionEntity>> transactions = transactionRepo.findByCategory(category);
        if(transactions.isPresent()) {
            transactions.get().forEach(allTransactions::add);
            result = allTransactions.stream()
                    .filter(x -> x.getDate().toLocalDate().getYear() == year)
                    .collect(Collectors.groupingBy(
                            x -> x.getDate().toLocalDate().getMonth().name() + "_" + x.getDate().toLocalDate().getYear(),
                            Collectors.collectingAndThen(
                                    Collectors.minBy(Comparator.comparing(TransactionEntity::getAmount)),
                                    optional -> optional.map(TransactionEntity::getAmount).orElse(0.0)
                            )

                    ));
        }
        return(new ResponseEntity<>(result, HttpStatus.OK));
    }

    @Override
    public ResponseEntity<TransactionDto> saveTransactions(TransactionRequestDto transactionRequestDto) {

        TransactionEntity transactionEntity  = transactionRepo.save(mapToEntity(transactionRequestDto));

        return new ResponseEntity<>(mapToResponseDto(transactionEntity), HttpStatus.CREATED);
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

         return TransactionDto.builder()
                .id(transactionEntity.getId())
                .date(transactionEntity.getDate())
                .vendor(transactionEntity.getVendor())
                .type(transactionEntity.getType())
                .amount(transactionEntity.getAmount())
                .category(transactionEntity.getCategory())
                .build();
    }

    private Date covertStringToDate(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy");
        return formatter.parse(date);
    }
}
