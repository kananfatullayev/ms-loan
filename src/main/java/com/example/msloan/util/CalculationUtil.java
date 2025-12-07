package com.example.msloan.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

import static java.math.BigDecimal.ONE;
import static java.math.RoundingMode.HALF_UP;

@UtilityClass
public class CalculationUtil {
    public BigDecimal calculateMonthlyAmount(BigDecimal loanAmount,
                                             BigDecimal annualInterestRate,
                                             BigDecimal duration) {
        BigDecimal monthlyInterestRate = annualInterestRate.divide(BigDecimal.valueOf(12 * 100), 10, HALF_UP);

        var durationInMonths = duration.intValue();

        BigDecimal numerator = monthlyInterestRate.multiply((ONE.add(monthlyInterestRate)).pow(durationInMonths));
        BigDecimal denominator = (ONE.add(monthlyInterestRate)).pow(durationInMonths).subtract(ONE);

        return loanAmount.multiply(numerator).divide(denominator, HALF_UP);
    }
}
