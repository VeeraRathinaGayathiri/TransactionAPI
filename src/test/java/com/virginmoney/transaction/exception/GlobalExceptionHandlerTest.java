package com.virginmoney.transaction.exception;

import com.virginmoney.transaction.model.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp(){
        exceptionHandler = new GlobalExceptionHandler();
    }

     @Test
    void testTransactionNotFoundException(){

        TransactionNotFound exception = new TransactionNotFound("No transaction found");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTransactionNotFound(exception);

         assertAll(
                 () -> assertThat(response).isNotNull(),
                 () -> assertThat(response.getBody().status()).isEqualTo(404),
                 () -> assertThat(response.getBody().message()).isEqualTo("No transaction found")
         );
    }

    @Test
    void testDatabaseFetchException(){

        DatabaseFetchException exception = new DatabaseFetchException("Error connecting to database");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDatabaseFetchException(exception);

        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.getBody().status()).isEqualTo(500),
                () -> assertThat(response.getBody().message()).isEqualTo("Error connecting to database")
        );
    }

}