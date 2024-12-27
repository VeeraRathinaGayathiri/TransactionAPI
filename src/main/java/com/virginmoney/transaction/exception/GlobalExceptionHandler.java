package com.virginmoney.transaction.exception;

import com.virginmoney.transaction.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(TransactionNotFound.class)
    public ResponseEntity<ErrorResponse> handleTransactionNotFound(TransactionNotFound exception){
        logger.error("Transaction Not found - Exception handler invoked");
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), exception.getMessage());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DatabaseFetchException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseFetchException(DatabaseFetchException exception){
        logger.error("Error fetching data - Exception handler invoked");
        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
