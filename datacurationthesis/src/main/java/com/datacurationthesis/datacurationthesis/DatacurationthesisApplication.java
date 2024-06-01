package com.datacurationthesis.datacurationthesis;

import com.datacurationthesis.datacurationthesis.logger.LoggerController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class DatacurationthesisApplication {

	public static void main(String[] args) {
		SpringApplication.run(DatacurationthesisApplication.class, args);
		LoggerController.info("Server started");
	}

}
