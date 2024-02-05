package com.anchor.global.log;

import com.anchor.global.exception.AnchorException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ExceptionLoggingAspect {

  @AfterThrowing(pointcut = "execution(* com.anchor..*.*(..))", throwing = "ex")
  public void logAnchorException(AnchorException ex) {
    log.error(ex.getMessage(), ex);
  }

}
