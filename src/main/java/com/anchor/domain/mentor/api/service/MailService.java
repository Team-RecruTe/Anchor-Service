package com.anchor.domain.mentor.api.service;

import com.anchor.domain.mentor.api.controller.request.MailDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
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
  @Value("${spring.mail.username}")
  String sender;

  private final JavaMailSender javaMailSender;

  @Async
  public void sendMail(MailDto mailDto) {
    MimeMessage message = javaMailSender.createMimeMessage();
    try {
      message.setFrom(sender);
      message.setRecipients(MimeMessage.RecipientType.TO, mailDto.getReceiver());
      message.setSubject(mailDto.getTitle());
      message.setText(mailDto.getContent(), "UTF-8", "html");
      javaMailSender.send(message);
    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }
}
