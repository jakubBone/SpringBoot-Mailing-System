package com.jakubbone;

import com.jakubbone.utils.EnvConfigLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class CurrencyExchangeApp {
	public static void main(String[] args) {
		//EnvConfigLoader.loadEnvVariables();
		SpringApplication.run(CurrencyExchangeApp.class, args);
	}
}
