package com.bicosteve.api_gateway;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class Main {
	private static final Logger log = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Main.class);
		app.setLogStartupInfo(false);
		app.setBannerMode(Banner.Mode.OFF);

		var context = app.run(args);

		String port = context.getEnvironment().getProperty("local.server.port");
//		SpringApplication.run(Main.class, args);
		log.info("-----------------------------------");
		log.info("Service is running on port {}",port);
		log.info("-----------------------------------");
	}
}
