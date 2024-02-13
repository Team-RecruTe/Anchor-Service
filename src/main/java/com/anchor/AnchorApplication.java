package com.anchor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling
@EnableRetry
@SpringBootApplication
public class AnchorApplication {

  public static void main(String[] args) {
    SpringApplication.run(AnchorApplication.class, args);
  }

}