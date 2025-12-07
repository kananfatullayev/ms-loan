package com.example.msloan.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponseDto {

    private Integer id;
    private Integer userId;
    private BigDecimal amount;
    private BigDecimal monthlyAmount;
    private BigDecimal duration;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

