package com.datacurationthesis.datacurationthesis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DatacurationthesisApplication {

	public static void main(String[] args) {
		SpringApplication.run(DatacurationthesisApplication.class, args);
		Logger logger = LogManager.getLogger(DatacurationthesisApplication.class);
		logger.info("Server started");
	}

}
