package com.virginmoney.transaction.repo;

import com.virginmoney.transaction.model.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepo extends JpaRepository<TransactionEntity, Long> {
    Optional<List<TransactionEntity>>  findByCategory(String category);
}
