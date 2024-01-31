package com.anchor.domain.mentor.api.service;

import com.anchor.global.mail.AsyncMailSender;
import com.anchor.global.mail.MailMessage;
import com.anchor.global.mail.SimpleMailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService {

  private final AsyncMailSender asyncMailSender;

  public void sendAuthMail(String receiver, String emailCode) {
    MailMessage mailMessage = new SimpleMailMessage("Anchor-Service: 이메일 인증키입니다.", emailCode, receiver);
    asyncMailSender.sendMail(mailMessage);
  }

}
