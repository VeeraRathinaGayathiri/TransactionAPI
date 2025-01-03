package com.virginmoney.transaction.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@Builder
@Entity
@Table(name = "transaction")
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MMM/yyyy")
    private Date date;

    private String vendor;

    private TransactionType type;

    private double amount;

    private String category;

}
