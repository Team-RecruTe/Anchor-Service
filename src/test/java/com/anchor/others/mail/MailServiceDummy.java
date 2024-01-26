package com.anchor.others.mail;

import com.anchor.global.mail.AsyncMailSender;
import com.anchor.global.mail.MailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MailServiceDummy {

  private final AsyncMailSender asyncMailSender;

  public void sendMail(MailMessage mailMessage) {
    asyncMailSender.sendMail(mailMessage);
  }

}
