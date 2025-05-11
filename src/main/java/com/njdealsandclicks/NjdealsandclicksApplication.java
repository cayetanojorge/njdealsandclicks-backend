package com.njdealsandclicks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

// @SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@SpringBootApplication
@EnableConfigurationProperties
public class NjdealsandclicksApplication {

	public static void main(String[] args) {
		SpringApplication.run(NjdealsandclicksApplication.class, args);
	}

}
