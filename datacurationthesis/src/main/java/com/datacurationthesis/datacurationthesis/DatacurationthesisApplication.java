package com.datacurationthesis.datacurationthesis;

import com.datacurationthesis.datacurationthesis.logger.LoggerController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;



@SpringBootApplication
@EntityScan("com.datacurationthesis.datacurationthesis.entity")
public class DatacurationthesisApplication {

	public static void main(String[] args) {
		SpringApplication.run(DatacurationthesisApplication.class, args);
		LoggerController.info("Server started");
	}
}
