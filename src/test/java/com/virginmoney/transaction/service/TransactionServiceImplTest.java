package com.virginmoney.transaction.service;


import com.virginmoney.transaction.dto.TransactionDto;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
     // MockitoAnnotations.openMocks(this);
        transactionService = new TransactionServiceImpl(transactionRepo);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
        allTransactions = Arrays.asList(
                new TransactionEntity(1, new Date(dateFormat.parse("28/Oct/2020").getTime()), "CYBG",
                        "direct debit", 600, "MyMonthlyDD"),
                new TransactionEntity(2, new Date(dateFormat.parse("30/Oct/2020").getTime()), "Morrisons",
                        "direct debit", 54.6, "MyMonthlyDD"),
                new TransactionEntity(3, new Date(dateFormat.parse("28/Oct/2021").getTime()), "CYBG",
                        "direct debit", 600, "MyMonthlyDD"),
                new TransactionEntity(4, new Date(dateFormat.parse("04/Apr/2021").getTime()), "ALDI",
                        "direct debit", 428, "MyMonthlyDD"),
                new TransactionEntity(5, new Date(dateFormat.parse("28/Nov/2020").getTime()), "PureGym",
                        "direct debit", 40, "MyMonthlyDD"));
          }

    @Test
    void getAllTransactions_NoTransactionForCategory_shouldthrowException(){
        String category = "Vacation";

        when(transactionRepo.findByCategory("Vacation")).thenReturn(Optional.of(new ArrayList<TransactionEntity>()));

        Exception exception = assertThrows(TransactionNotFound.class, () -> transactionService.getAllTransactions(category));

        assertThat(exception.getMessage()).isEqualTo("No transactions found for the category : Vacation");

    }

    @Test
    void getAllTransactions_TransactionFound_shouldReturnTransactionRecords()  {
        String category = "MyMonthlyDD";

        when(transactionRepo.findByCategory("MyMonthlyDD")).thenReturn(Optional.of(allTransactions));

        ResponseEntity<List<TransactionDto>> response = transactionService.getAllTransactions(category);

        List<TransactionDto> result = response.getBody();
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody().isEmpty()).isFalse(),
                () -> assertThat(response.getBody().size()).isEqualTo(5),
                () -> assertThat(result.get(0).amount()).isEqualTo(600)
        );

    }

    @Test
    void getTotalSpend_NoTransactionCategory_shouldThrowException() {

        String category = "Vacation";

        when(transactionRepo.findByCategory("Vacation")).thenReturn(Optional.of(new ArrayList<TransactionEntity>()));
        Exception exception = assertThrows(TransactionNotFound.class, () -> transactionService.getTotalSpend(category));

        assertThat(exception.getMessage()).isEqualTo("No transactions found for the category : Vacation");

    }


    @Test
    void getTotalSpend_TransactionFound_shouldReturnTotalSpend() {

        String category = "MyMonthlyDD";

        when(transactionRepo.findByCategory("MyMonthlyDD")).thenReturn(Optional.of(allTransactions));

        ResponseEntity<Double> response = transactionService.getTotalSpend(category);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody().isNaN()).isFalse(),
                () -> assertThat(response.getBody()).isEqualTo(1722.6)
        );

    }
    @Test
    void getMonthlyAverage_NoTransactionCategory_shouldThrowException() {
        String category = "Vacation";

        when(transactionRepo.findByCategory("Vacation")).thenReturn(Optional.of(new ArrayList<TransactionEntity>()));
        Exception exception = assertThrows(TransactionNotFound.class, () -> transactionService.getMonthlyAverage(category));

        assertThat(exception.getMessage()).isEqualTo("No transactions found for the category : Vacation");

    }


    @Test
    void getMonthlyAverage_TransactionFound_shouldReturnMonthlyAverage() {

        String category = "MyMonthlyDD";

        when(transactionRepo.findByCategory("MyMonthlyDD")).thenReturn(Optional.of(allTransactions));

        ResponseEntity<Map<String, Double>> response = transactionService.getMonthlyAverage(category);

        Map<String, Double> result = response.getBody();

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody().isEmpty()).isFalse(),
                () -> assertThat(result.size()).isEqualTo(4),
                () -> assertThat(result.get("OCTOBER_2020")).isEqualTo(327.3)
        );

    }

    @Test
    void getHighestSpend_NoTransactionCategory_shouldThrowException() {
        String category = "Vacation";
        int year = 2020;

        when(transactionRepo.findByCategory("Vacation")).thenReturn(Optional.of(new ArrayList<TransactionEntity>()));
        Exception exception = assertThrows(TransactionNotFound.class, () -> transactionService.getHighestSpend(category, year));

        assertThat(exception.getMessage()).isEqualTo("No transactions found for the category : Vacation");

    }

    @Test
    void getHighestSpend_TransactionFound_shouldReturnHighestSpend() {
        String category = "MyMonthlyDD";
        int year = 2020;

        when(transactionRepo.findByCategory("MyMonthlyDD")).thenReturn(Optional.of(allTransactions));
        ResponseEntity<Double> response = transactionService.getHighestSpend(category, year);
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody().isNaN()).isFalse(),
                () -> assertThat(response.getBody()).isEqualTo(600)
        );

    }

    @Test
    void getLowestSpend_NoTransactionCategory_shouldThrowException() {

        String category = "Vacation";
        int year = 2020;

        when(transactionRepo.findByCategory("Vacation")).thenReturn(Optional.of(new ArrayList<TransactionEntity>()));
        Exception exception = assertThrows(TransactionNotFound.class, () -> transactionService.getLowestSpend(category, year));

        assertThat(exception.getMessage()).isEqualTo("No transactions found for the category : Vacation");

    }


    @Test
    void getLowestSpend_TransactionFound_shouldReturnLowestSpend() {
        String category = "MyMonthlyDD";
        int year = 2020;

        when(transactionRepo.findByCategory("MyMonthlyDD")).thenReturn(Optional.of(allTransactions));
        ResponseEntity<Double> response = transactionService.getLowestSpend(category, year);
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody().isNaN()).isFalse(),
                () -> assertThat(response.getBody()).isEqualTo(40.0)
        );

    }

    @Test
    void allMethods_DBConnectionIssue_shouldThrowDatabaseFetchException(){

        when(transactionRepo.findByCategory(anyString())).thenThrow(new DatabaseFetchException("connection error"));

        Exception caughtException = assertThrows(DatabaseFetchException.class, () -> transactionService.getAllTransactions("MyMonthlyDD"));

        assertThat(caughtException.getMessage()).isEqualTo("Error fetching transactions from database");

    }
}