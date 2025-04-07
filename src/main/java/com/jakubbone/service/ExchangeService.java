package com.jakubbone.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service // Marks as a Spring service component
public class ExchangeService {
    private final Map<String, BigDecimal> currencyRates;

    public ExchangeService() {
        currencyRates = new HashMap<>();
        currencyRates.put("USD", new BigDecimal("4.10"));
        currencyRates.put("EUR", new BigDecimal("4.00"));
        currencyRates.put("PLN", BigDecimal.ONE);
    }

    public BigDecimal exchange(BigDecimal amount, String from, String to){
        if(from.equals(to)){
            return amount;
        }

        BigDecimal fromCurrency = currencyRates.get(from);
        BigDecimal toCurrency = currencyRates.get(to);

        if(fromCurrency == null ||  toCurrency == null){
            throw new IllegalArgumentException("unknown currency:" +
                    (fromCurrency == null ? from: to ));
        }

        // Step 1: Convert from source currency to PLN
        BigDecimal plnAmount = amount.multiply(currencyRates.get(from));

        // Step 1: Divide by target rate to convert PLN to the target currency
        return plnAmount.divide(currencyRates.get(to), 2, RoundingMode.HALF_UP);
    }
}
