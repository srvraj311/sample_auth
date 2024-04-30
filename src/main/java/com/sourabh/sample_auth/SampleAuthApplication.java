package com.sourabh.sample_auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableAutoConfiguration()
public class SampleAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(SampleAuthApplication.class, args);
	}

}
