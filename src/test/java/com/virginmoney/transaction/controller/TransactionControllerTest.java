package com.virginmoney.transaction.controller;

import com.virginmoney.transaction.dto.TransactionDto;
import com.virginmoney.transaction.dto.TransactionRequestDto;
import com.virginmoney.transaction.service.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    TransactionServiceImpl transactionService;

    @InjectMocks
    TransactionController transactionController;

    private List<TransactionDto> allTransactions;
    @BeforeEach
    void setUp() throws ParseException {
       transactionController = new TransactionController(transactionService);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
        allTransactions = Arrays.asList(
                new TransactionDto(1, new Date(dateFormat.parse("28/Oct/2020").getTime()), "CYBG",
                        "direct debit", 600, "MyMonthlyDD"),
                new TransactionDto(2, new Date(dateFormat.parse("30/Oct/2020").getTime()), "Morrisons",
                        "direct debit", 54.6, "MyMonthlyDD"),
                new TransactionDto(3, new Date(dateFormat.parse("28/Oct/2021").getTime()), "CYBG",
                        "direct debit", 600, "MyMonthlyDD"),
                new TransactionDto(4, new Date(dateFormat.parse("04/Apr/2021").getTime()), "ALDI",
                        "direct debit", 428, "MyMonthlyDD"),
                new TransactionDto(5, new Date(dateFormat.parse("28/Nov/2020").getTime()), "PureGym",
                        "direct debit", 40, "MyMonthlyDD"));
    }

    @Test
    void getAllTransactions_TransactionFoundForCategory_shouldReturnTransactions() {
        String category = "MyMonthlyDD";

        when(transactionService.getAllTransactions(category)).thenReturn(new ResponseEntity<>(allTransactions, HttpStatus.OK));

        ResponseEntity<List<TransactionDto>> response = transactionController.getAllTransactions(category);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(allTransactions);
        verify(transactionService, times(1)).getAllTransactions(category);

    }

    @Test
    void getTotalSpend_TransactionFoundForCategory_shouldReturnTotalSpend() {
        String category = "MyMonthlyDD";
        Double result = allTransactions.stream()
                .mapToDouble(TransactionDto::amount)
                .reduce(0.0, Double::sum);

        when(transactionService.getTotalSpend(category)).thenReturn(new ResponseEntity<>(result,HttpStatus.OK));
        ResponseEntity<Double> response = transactionController.getTotalSpend(category);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(result);
        verify(transactionService, times(1)).getTotalSpend(category);
    }

    @Test
    void getMonthlyAverage_TransactionFoundForCategory_shouldReturnMonthlyAverage() {
        String category = "MyMonthlyDD";
        Map<String, Double> result = new HashMap<>() {{
            put("OCTOBER_2020", 664.6);
            put("OCTOBER_2021", 600.0);
        }};

        when(transactionService.getMonthlyAverage(category)).thenReturn(new ResponseEntity<>(result,HttpStatus.OK));
        ResponseEntity<Map<String, Double>> response = transactionController.getMonthlyAverage(category);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(result);
        verify(transactionService, times(1)).getMonthlyAverage(category);
    }

    @Test
    void getHighestSpend_TransactionFoundForCategory_shouldReturnHighestSpendOftheYear() {
        String category = "MyMonthlyDD";
        int year = 2020;

        when(transactionService.getHighestSpend(category,year)).thenReturn(new ResponseEntity<>(600.0,HttpStatus.OK));
        ResponseEntity<Double> response = transactionController.getHighestSpend(category, year);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(600.0);
        verify(transactionService, times(1)).getHighestSpend(category,year);
    }

    @Test
    void getLowestSpend_TransactionFoundForCategory_shouldReturnLowestSpendOftheYear() {
        String category = "MyMonthlyDD";
        int year = 2020;

        when(transactionService.getLowestSpend(category,year)).thenReturn(new ResponseEntity<>(40.0,HttpStatus.OK));
        ResponseEntity<Double> response = transactionController.getLowestSpend(category, year);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(40.0);
        verify(transactionService, times(1)).getLowestSpend(category,year);
    }

    @Test
    void saveTransactions_TransactionsSave_Successful() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
        TransactionRequestDto input = new TransactionRequestDto(new Date(dateFormat.parse("28/Oct/2020").getTime()), "CYBG",
                "direct debit", 600, "MyMonthlyDD");

        when(transactionService.saveTransactions(input)).thenReturn(new ResponseEntity<>(allTransactions.get(1), HttpStatus.CREATED));

        ResponseEntity<TransactionDto> response = transactionController.saveTransactions(input);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(allTransactions.get(1));
        verify(transactionService, times(1)).saveTransactions(input);


    }

}