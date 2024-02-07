package com.anchor.others.mail;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.anchor.global.db.DBConfig;
import com.anchor.global.mail.AsyncMailSender;
import com.anchor.global.mail.MailMessage;
import com.anchor.global.mail.SimpleMailMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@DisplayName("비동기 메일 전송 테스트")
@Import({JavaMailSenderImpl.class, DBConfig.class})
@ActiveProfiles("test")
@SpringBootTest
class AsyncMailServiceTest {

  @Value("${email.test}")
  private String testEmail;

  @SpyBean
  private AsyncMailSender asyncMailSender;

  @Autowired
  private MailServiceDummy mailService;

  @DisplayName("메일 전송에 대한 요청을 비동기로 처리합니다.")
  @Test
  void sendMail() throws InterruptedException {
    // given
    String title = "테스트 메일 제목입니다.";
    String contents = "테스트 메일 내용입니다.";
    MailMessage mailMessage = new SimpleMailMessage(title, contents, testEmail);

    // when
    long start = System.currentTimeMillis();
    mailService.sendMail(mailMessage);
    long end = System.currentTimeMillis();

    Thread.sleep(100);

    // then
    verify(asyncMailSender, times(1)).sendMail(any(MailMessage.class));
    log.info("호출에 걸리는 시간: {}ms", end - start);
  }

}