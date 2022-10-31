package com.moysklad.demo;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.moysklad.demo.repository")
@OpenAPIDefinition(info = @Info(title = "MoySklad API", version = "1.0.0"))
public class MoySkladApplication {
	public static void main(String[] args) {
		SpringApplication.run(MoySkladApplication.class, args);
	}

}
