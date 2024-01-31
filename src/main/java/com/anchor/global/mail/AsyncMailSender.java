package com.anchor.global.mail;

import com.anchor.global.exception.type.mail.MailSendException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AsyncMailSender {

  private final JavaMailSender javaMailSender;

  @Value("${spring.mail.username}")
  private String sender;

  @Async("emailAsync")
  public void sendMail(MailMessage mailMessage) {
    MimeMessage message = createMimeMessage(mailMessage);
    send(message);
  }

  @Async("emailAsync")
  public void sendMails(List<MailMessage> mailMessages) {
    MimeMessage[] mimeMessages = (MimeMessage[]) mailMessages.stream()
        .map(this::createMimeMessage)
        .toArray();
    send(mimeMessages);
  }

  private void send(MimeMessage... mimeMessages) {
    try {
      javaMailSender.send(mimeMessages);
    } catch (MailException e) {
      throw new MailSendException(e);
    }
  }

  private MimeMessage createMimeMessage(MailMessage mailMessage) {
    MimeMessage message = javaMailSender.createMimeMessage();
    try {
      message.setFrom(sender);
      message.setRecipients(MimeMessage.RecipientType.TO, mailMessage.getReceiver());
      message.setSubject(mailMessage.getTitle());
      message.setText(mailMessage.getContents(), "UTF-8", "html");
    } catch (MessagingException e) {
      log.info("MailSender - Failed to create Message : {0}", e);
    }
    return message;
  }

}
