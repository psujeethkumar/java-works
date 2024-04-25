package com.innovitiers.log4j.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CustomConfigurationFactoryTest {

	@BeforeClass
	public static void setUp() {
		System.out.println("In Before class");
		System.setProperty("log4j.configurationFactory", CustomConfigurationFactory.class.getName());
		CustomConfigurationFactory customConfigurationFactory = new CustomConfigurationFactory();
		ConfigurationFactory.setConfigurationFactory(customConfigurationFactory);
		System.out.println("customConfigurationFactory is set now");
	}

	@Test
	public void givenDirectConfiguration_whenUsingFlowMarkers_ThenLogsCorrectly() {
		Logger logger = LogManager.getLogger(this.getClass());
		Marker markerContent = MarkerManager.getMarker("FLOW");
		for (int i = 0; i < 1000000; i++) {

			logger.debug(markerContent, "Debug log message");
			logger.info(markerContent, "Info log message");
			logger.error(markerContent, "Error log message");
		}
	}

}
