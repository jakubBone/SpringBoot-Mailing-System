package com.jakubbone.version_info_service.unit;

import com.jakubbone.exception.UnsupportedCurrencyException;
import com.jakubbone.service.ExchangeService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeServiceTest {

    ExchangeService service = new ExchangeService();

    @Test
    void shouldReturnUnchangedCurrency_whenCurrencySame(){
        BigDecimal amount = new BigDecimal("100.00");

        BigDecimal result = service.exchange(amount, "PLN", "PLN");

        assertEquals(amount, result);
    }

    @Test
    void shouldReturnExchangedCurrency_whenCurrencyDifferent(){
        BigDecimal amount = new BigDecimal("100.00");

        BigDecimal expected = new BigDecimal("400.00");
        BigDecimal result = service.exchange(amount, "EUR", "PLN");

        assertEquals(expected, result);
    }

    @Test
    void shouldThrowException_whenUnsupportedCurrency(){
        BigDecimal amount = new BigDecimal("100.00");

        Exception exception = assertThrows(UnsupportedCurrencyException.class, () -> {
            service.exchange(amount, "GBP", "PLN");
        });

        assertTrue(exception.getMessage().contains("unknown currency: GBP"));
    }
}
