package com.anchor.global.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class AsyncLoggingAspect {

  @Around("@annotation(org.springframework.scheduling.annotation.Async) && execution(void sendMail(com.anchor.global.mail.MailMessage))")
  public Object logAsyncMethod(ProceedingJoinPoint joinPoint) throws Throwable {
    try {
      log.info("MailSender - Start to send");
      Object result = joinPoint.proceed();
      log.info("MailSender - Sent successfully");
      return result;
    } catch (Exception ex) {
      log.info("MailSender - Failed to send");
      throw ex;
    }
  }

}
