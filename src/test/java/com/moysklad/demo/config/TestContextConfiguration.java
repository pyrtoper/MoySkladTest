package com.moysklad.demo.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@TestConfiguration
public class TestContextConfiguration {
  @Bean
  public MethodValidationPostProcessor bean() {
    return new MethodValidationPostProcessor();
  }
}
