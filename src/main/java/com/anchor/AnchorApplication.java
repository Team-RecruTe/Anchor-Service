package com.anchor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class AnchorApplication {

  public static void main(String[] args) {
    SpringApplication.run(AnchorApplication.class, args);
  }

}
