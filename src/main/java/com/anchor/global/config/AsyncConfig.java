package com.anchor.global.config;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@EnableRetry
@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {

  @Bean(name = "emailAsync")
  public Executor threadPoolTaskExecutor() {
    final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(4);
    executor.setQueueCapacity(8);
    executor.setThreadNamePrefix("executor-");
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.initialize();
    return executor;
  }

  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return new AsyncEmailUncaughtExceptionHandler();
  }

  static class AsyncEmailUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
      log.info("Exception message: " + ex.getMessage());
      log.info("Method name: " + method.getName());
      Arrays.stream(params)
          .sequential()
          .forEach(param -> log.info("Parameter value: " + param));
    }

  }

}
