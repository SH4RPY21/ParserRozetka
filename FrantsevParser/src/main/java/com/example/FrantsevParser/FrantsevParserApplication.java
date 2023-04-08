package com.example.FrantsevParser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.bonigarcia.wdm.WebDriverManager;

@SpringBootApplication
public class FrantsevParserApplication {

	public static void main(String[] args) {

		SpringApplication.run(FrantsevParserApplication.class, args);
		WebDriverManager.chromedriver().setup();
	}

}
