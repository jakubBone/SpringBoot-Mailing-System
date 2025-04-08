package com.jakubbone.controller;

import com.jakubbone.domain.model.ExchangeRequest;
import com.jakubbone.service.ExchangeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController // Informs Spring that this class will handle REST requests and return JSON responses
@RequestMapping("api/currency") // Sets a common prefix for all endpoints in this controller
public class ExchangeController {
    private final ExchangeService service;

    public ExchangeController(ExchangeService service) {
        this.service = service;
    }

    /*
     # Handles HTTP POST request for '/api/currency/exchange'
     # Accepts input in JSON format (via @RequestBody).
     # 'req CurrencyRequest' containing amount, source, and target currency
      */
    @PostMapping("/exchange")
    public ResponseEntity<?> exchangeCurrency(@RequestBody ExchangeRequest req){
        // @RequestBody maps the incoming JSON request body to a CurrencyRequest object
        try{
            if(req.getAmount() == null || req.getFrom() == null || req.getTo() == null ||
                    req.getFrom().isBlank() || req.getTo().isBlank() ){
                return ResponseEntity.badRequest().body("Invalid request: amount/from/to must are required");
            }
            BigDecimal result = service.exchange(req.getAmount(), req.getFrom(), req.getTo());
            return ResponseEntity.ok(result);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /*
    ResponseEntity represents the full HTTP response:

    # Status: e.g. 200 OK, 400 Bad Request, 404 Not Found
    # Headers: additional response metadata
    # Body: actual response content (e.g. result of conversion)
     */
}
