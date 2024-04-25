package com.innovitiers.log4j.client;

import java.io.IOException;
import java.net.URI;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.LoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Plugin(name = "CustomConfigurationFactory", category = ConfigurationFactory.CATEGORY)
@Order(50)
public class CustomConfigurationFactory extends ConfigurationFactory {

	private static Logger log = LogManager.getLogger(CustomConfigurationFactory.class);

	@Override
	public Configuration getConfiguration(LoggerContext loggerContext, ConfigurationSource source) {
		return getConfiguration(loggerContext, source.toString(), null);
	}

	@Override
	protected String[] getSupportedTypes() {
		return new String[] { "*" };
	}

	@Override
	public Configuration getConfiguration(final LoggerContext loggerContext, final String name, final URI configLocation) {
		ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
		return createConfiguration(name, builder);
	}

	Configuration createConfiguration(final String name, ConfigurationBuilder<BuiltConfiguration> builder) {

		AppenderComponentBuilder file = builder.newAppender("log", "File");
		file.addAttribute("fileName", "logging.log");

		LayoutComponentBuilder standard = builder.newLayout("PatternLayout");
//		standard.addAttribute("pattern", "%d [%t] %-5level: %msg%n%throwable");

		file.add(standard);

		builder.add(file);

		LoggerComponentBuilder logger = builder.newLogger("com", Level.ALL);
		logger.add(builder.newAppenderRef("log"));
		logger.addAttribute("additivity", false);

		// builder.add(logger);

		RootLoggerComponentBuilder rootLogger = builder.newRootLogger(Level.ALL);
		rootLogger.add(builder.newAppenderRef("log"));

		builder.add(rootLogger);

		try {
			builder.writeXmlConfiguration(System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return builder.build();

	}
}
