package com.jakubbone;

import com.jakubbone.utils.EnvConfigLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ClientServiceApp {
	public static void main(String[] args) {
		EnvConfigLoader.loadEnvVariables();
		SpringApplication.run(ClientServiceApp.class, args);
	}
}
