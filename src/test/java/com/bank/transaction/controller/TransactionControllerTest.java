package com.virginmoney.transaction.controller;

import com.virginmoney.transaction.dto.StatisticsDto;
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

    private List<TransactionDto> transactionsByCategory;
    @BeforeEach
    void setUp() throws ParseException {
       transactionController = new TransactionController(transactionService);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
        transactionsByCategory = Arrays.asList(
                new TransactionDto(4, new Date(dateFormat.parse("04/Apr/2021").getTime()), "ALDI",
                        "direct_debit", 428, "MyMonthlyDD"),
                new TransactionDto(3, new Date(dateFormat.parse("28/Oct/2021").getTime()), "CYBG",
                        "internet", 600, "MyMonthlyDD"),
                new TransactionDto(1, new Date(dateFormat.parse("28/Oct/2020").getTime()), "CYBG",
                        "direct_debit", 600, "MyMonthlyDD"),
                new TransactionDto(2, new Date(dateFormat.parse("30/Oct/2020").getTime()), "Morrisons",
                        "cardt", 54.6, "MyMonthlyDD"),
                new TransactionDto(5, new Date(dateFormat.parse("28/Nov/2020").getTime()), "PureGym",
                        "direct_debit", 40, "MyMonthlyDD"));
    }

    @Test
    void getLatestByCategory_TransactionFoundForCategory_shouldReturnTransactions() {
        String category = "MyMonthlyDD";

        when(transactionService.getLatestByCategory(category)).thenReturn(new ResponseEntity<>(transactionsByCategory, HttpStatus.OK));

        ResponseEntity<List<TransactionDto>> response = transactionController.getLatestByCategory(category);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(transactionsByCategory);
        verify(transactionService, times(1)).getLatestByCategory(category);

    }

    @Test
    void getTotalSpendByCategory_TransactionFoundForCategory_shouldReturnTotalSpend() {
        String category = "MyMonthlyDD";
        Double result = transactionsByCategory.stream()
                .mapToDouble(TransactionDto::amount)
                .reduce(0.0, Double::sum);

        when(transactionService.getTotalSpendByCategory(category)).thenReturn(new ResponseEntity<>(result,HttpStatus.OK));
        ResponseEntity<Double> response = transactionController.getTotalSpendByCategory(category);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(result);
        verify(transactionService, times(1)).getTotalSpendByCategory(category);
    }

    @Test
    void getMonthlyAverageByCategory_TransactionFoundForCategory_shouldReturnMonthlyAverage() {
        String category = "MyMonthlyDD";
        Map<String, Double> result = new HashMap<>() {{
            put("OCTOBER_2020", 664.6);
            put("OCTOBER_2021", 600.0);
        }};

        when(transactionService.getMonthlyAverageByCategory(category)).thenReturn(new ResponseEntity<>(result,HttpStatus.OK));
        ResponseEntity<Map<String, Double>> response = transactionController.getMonthlyAverageByCategory(category);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(result);
        verify(transactionService, times(1)).getMonthlyAverageByCategory(category);
    }

    @Test
    void getYearlyStatisticsByCategory_TransactionFoundForCategoryInGivenYear_shouldReturnHighestSpendOftheYear() {
        String category = "MyMonthlyDD";
        int year = 2020;
        // MyMonthlyDD - 2020 - statistics : min - 40 , max - 600, avg - 231.533.
        StatisticsDto mockResponse = StatisticsDto.builder()
                .average_spend(231.533)
                .highest_spend(600.0)
                .lowest_spend(40.0)
                .build();


        when(transactionService.getYearlyStatisticsByCategory(category,year)).thenReturn(new ResponseEntity<>(mockResponse,HttpStatus.OK));
        ResponseEntity<StatisticsDto> response = transactionController.getYearlyStatisticsByCategory(category, year);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(mockResponse);
        verify(transactionService, times(1)).getYearlyStatisticsByCategory(category,year);
    }

    @Test
    void saveTransactions_TransactionsSave_Successful() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
        List<TransactionRequestDto> input = Arrays.asList(new TransactionRequestDto(new Date(dateFormat.parse("28/Oct/2020").getTime()),
                                            "CYBG", "card", 600, "MyMonthlyDD"),
                                            new TransactionRequestDto(new Date(dateFormat.parse("28/Oct/2022").getTime()),
                                                    "CYBG","direct_debit", 200, "Groceries")
        );

        when(transactionService.saveTransactions(input)).thenReturn(new ResponseEntity<>(transactionsByCategory, HttpStatus.CREATED));

        ResponseEntity<List<TransactionDto>> response = transactionController.saveTransactions(input);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(transactionsByCategory);
        verify(transactionService, times(1)).saveTransactions(input);

    }

}