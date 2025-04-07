package com.jakubbone.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor // @NoArgsConstructor - Jackson requires no args constructor to create JSON request
public class ExchangeRequest {
    private BigDecimal amount;
    private String from;
    private String to;
}
