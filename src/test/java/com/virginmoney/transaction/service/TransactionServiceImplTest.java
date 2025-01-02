package com.virginmoney.transaction.service;


import com.virginmoney.transaction.dto.StatisticsDto;
import com.virginmoney.transaction.dto.TransactionDto;
import com.virginmoney.transaction.model.TransactionType;
import com.virginmoney.transaction.exception.DatabaseFetchException;
import com.virginmoney.transaction.exception.TransactionNotFound;
import com.virginmoney.transaction.model.TransactionEntity;
import com.virginmoney.transaction.repo.TransactionRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepo transactionRepo;

    private  TransactionServiceImpl transactionService;

    private List<TransactionEntity> allTransactions;

    @BeforeEach
    void setUp() throws ParseException {
        transactionService = new TransactionServiceImpl(transactionRepo);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
        allTransactions = Arrays.asList(
                new TransactionEntity(1, new Date(dateFormat.parse("28/Oct/2020").getTime()), "CYBG",
                        TransactionType.CARD, 600, "MyMonthlyDD"),
                new TransactionEntity(2, new Date(dateFormat.parse("30/Oct/2020").getTime()), "Morrisons",
                        TransactionType.INTERNET, 54.6, "MyMonthlyDD"),
                new TransactionEntity(3, new Date(dateFormat.parse("28/Oct/2021").getTime()), "CYBG",
                        TransactionType.CARD, 600, "MyMonthlyDD"),
                new TransactionEntity(4, new Date(dateFormat.parse("04/Apr/2021").getTime()), "ALDI",
                        TransactionType.BANK_TRANSFER, 428, "MyMonthlyDD"),
                new TransactionEntity(5, new Date(dateFormat.parse("28/Nov/2020").getTime()), "PureGym",
                        TransactionType.DIRECT_DEBIT, 40, "MyMonthlyDD"));
        // MyMonthlyDD - 2020 - statistics : min - 40 , max - 600, avg - 231.533.
          }

    @Test
    void getLatestByCategory_NoTransactionForCategory_shouldthrowException(){
        String category = "Vacation";

        when(transactionRepo.findByCategory("Vacation")).thenReturn(Optional.of(new ArrayList<TransactionEntity>()));

        Exception exception = assertThrows(TransactionNotFound.class, () -> transactionService.getLatestByCategory(category));

        assertThat(exception.getMessage()).isEqualTo("No transactions found for the category : Vacation");

    }

    @Test
    void getLatestByCategory_TransactionFound_shouldReturnTransactionRecords()  {
        String category = "MyMonthlyDD";

        when(transactionRepo.findByCategory("MyMonthlyDD")).thenReturn(Optional.of(allTransactions));

        ResponseEntity<List<TransactionDto>> response = transactionService.getLatestByCategory(category);

        List<TransactionDto> result = response.getBody();
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody()).isNotEmpty(),
                () -> assertThat(response.getBody()).hasSize(5),
                () -> assertThat(result.get(0).amount()).isEqualTo(600)
        );

    }

    @Test
    void getTotalSpendByCategory_NoTransactionCategory_shouldThrowException() {

        String category = "Vacation";

        when(transactionRepo.findByCategory("Vacation")).thenReturn(Optional.of(new ArrayList<TransactionEntity>()));
        Exception exception = assertThrows(TransactionNotFound.class, () -> transactionService.getTotalSpendByCategory(category));

        assertThat(exception.getMessage()).isEqualTo("No transactions found for the category : Vacation");

    }

    @Test
    void getTotalSpendByCategory_TransactionFound_shouldReturnTotalSpend() {

        String category = "MyMonthlyDD";

        when(transactionRepo.findByCategory("MyMonthlyDD")).thenReturn(Optional.of(allTransactions));

        ResponseEntity<Double> response = transactionService.getTotalSpendByCategory(category);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody()).isNotNull(),
                () -> assertThat(response.getBody()).isEqualTo(1722.6)
        );

    }
    @Test
    void getMonthlyAverageByCategory_NoTransactionCategory_shouldThrowException() {
        String category = "Vacation";

        when(transactionRepo.findByCategory("Vacation")).thenReturn(Optional.of(new ArrayList<TransactionEntity>()));
        Exception exception = assertThrows(TransactionNotFound.class, () -> transactionService.getMonthlyAverageByCategory(category));

        assertThat(exception.getMessage()).isEqualTo("No transactions found for the category : Vacation");

    }

    @Test
    void getMonthlyAverageByCategory_TransactionFound_shouldReturnMonthlyAverage() {

        String category = "MyMonthlyDD";

        when(transactionRepo.findByCategory("MyMonthlyDD")).thenReturn(Optional.of(allTransactions));

        ResponseEntity<Map<String, Double>> response = transactionService.getMonthlyAverageByCategory(category);

        Map<String, Double> result = response.getBody();

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody()).isNotEmpty(),
                () -> assertThat(result.keySet()).hasSize(4),
                () -> assertThat(result).containsEntry("OCTOBER_2020", 327.3)
        );

    }

   @Test
    void getYearlyStatisticsByCategory_NoTransactionCategory_shouldThrowException() {

        String category = "Vacation";
        int year = 2020;

        when(transactionRepo.findByCategory("Vacation")).thenReturn(Optional.of(new ArrayList<TransactionEntity>()));
        Exception exception = assertThrows(TransactionNotFound.class, () -> transactionService.getYearlyStatisticsByCategory(category, year));

        assertThat(exception.getMessage()).isEqualTo("No transactions found for the category : Vacation");

    }

    @Test
    void getYearlyStatisticsByCategory_CategoryFound_NoTransactionForGivenYear_shouldThrowException() {

        String category = "MyMonthlyDD";
        int year = 2022;

        when(transactionRepo.findByCategory("MyMonthlyDD")).thenReturn(Optional.of(allTransactions));
        Exception exception = assertThrows(TransactionNotFound.class, () -> transactionService.getYearlyStatisticsByCategory(category, year));

        assertThat(exception.getMessage()).isEqualTo("No transactions found for the category: MyMonthlyDD in 2022");

    }

    @Test
    void getYearlyStatisticsByCategory_TransactionFound_shouldReturnStatistics() {
        String category = "MyMonthlyDD";
        int year = 2020;

        when(transactionRepo.findByCategory("MyMonthlyDD")).thenReturn(Optional.of(allTransactions));
        ResponseEntity<StatisticsDto> response = transactionService.getYearlyStatisticsByCategory(category, year);


        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody()).isNotNull(),
                () -> assertThat(response.getBody().average_spend()).isEqualTo(231.53),
                () -> assertThat(response.getBody().lowest_spend()).isEqualTo(40.0),
                () -> assertThat(response.getBody().highest_spend()).isEqualTo(600.0)
                );

    }

    @Test
    void common_DBConnectionIssue_shouldThrowDatabaseFetchException(){

        when(transactionRepo.findByCategory(anyString())).thenThrow(new DatabaseFetchException("connection error"));

        Exception caughtException = assertThrows(DatabaseFetchException.class, () -> transactionService.getLatestByCategory("MyMonthlyDD"));

        assertThat(caughtException.getMessage()).isEqualTo("Error fetching transactions from database");

    }
}