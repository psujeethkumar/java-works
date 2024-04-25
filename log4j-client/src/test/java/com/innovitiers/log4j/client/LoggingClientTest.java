package com.innovitiers.log4j.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;

public class LoggingClientTest {

	private static Logger logger = LogManager.getLogger(LoggingClientTest.class);
//	final static LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
//	final static Configuration config = ctx.getConfiguration();

	public static void main(String[] args) {

//		final FileAppender fileAppender = FileAppender.newBuilder().withFileName("test.log").setName("Logger").setBufferSize(10).withAppend(true).setImmediateFlush(true).build();
//		fileAppender.start();

//		config.addAppender(fileAppender);
//		ctx.start();

		logger.debug("It is a debug logger.");
		logger.error("It is an error logger.");
		logger.fatal("It is a fatal logger.");
		logger.info("It is a info logger.");
		logger.trace("It is a trace logger.");
		logger.warn("It is a warn logger.");

	}

}
