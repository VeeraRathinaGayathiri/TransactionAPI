package com.virginmoney.transaction.dto;


import lombok.Builder;

@Builder
public record StatisticsDto(double lowest_spend, double highest_spend,  double average_spend) {
}
