package com.datacurationthesis.datacurationthesis.service;

import com.datacurationthesis.datacurationthesis.logger.LoggerController;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class DatabaseInfoService {

	@Autowired
	private DataSource dataSource;

	@EventListener(ContextRefreshedEvent.class)
	public void logDatabaseInfo() {
		try (Connection connection = dataSource.getConnection()) {
			DatabaseMetaData metaData = connection.getMetaData();
			LoggerController.info(metaData.getDatabaseProductName());
			LoggerController.info(metaData.getDatabaseProductVersion());
			LoggerController.info(metaData.getURL());
			LoggerController.info(metaData.getUserName());
		} catch (Exception e) {
			LoggerController.logException("Exception while getting database info", e);
		}
	}
}
