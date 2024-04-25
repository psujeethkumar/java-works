package com.skpakala.utilities.log4jclient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.lookup.MainMapLookup;

public class LoggerClient {

	private static final Logger logger = LogManager.getLogger(LoggerClient.class);

	public static void main(String[] args) {

		MainMapLookup.setMainArguments(new String[] {"fileDirectory", "logs", "fileName", "log/app.log"});

		for (int i = 0; i < 10000; i++) {
			logger.info("Rolling file appender example...");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
