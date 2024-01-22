package com.anchor.domain.mentor.api.service;

import com.anchor.domain.mentor.api.controller.request.MailDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService {

  private final JavaMailSender javaMailSender;
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  @Value("${spring.mail.username}")
  private String sender;

  @Async("emailAsync")
  public CompletableFuture<String> sendMail(MailDto mailDto) {
    CompletableFuture<String> future = new CompletableFuture<>();
    MimeMessage message = javaMailSender.createMimeMessage();
    scheduler.schedule(() -> future.completeExceptionally(new TimeoutException("작업 시간 초과")), 5, TimeUnit.SECONDS);
    try {
      message.setFrom(sender);
      message.setRecipients(MimeMessage.RecipientType.TO, mailDto.getReceiver());
      message.setSubject(mailDto.getTitle());
      message.setText(mailDto.getContent(), "UTF-8", "html");
      javaMailSender.send(message);
    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
    return future;
  }
}
