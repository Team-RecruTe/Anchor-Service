package com.anchor.global.mail;

import com.anchor.global.mail.MentoringMailMessage.MentoringMailMessageBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class MailMessage {

  private final String title;
  private final String contents;
  private final String receiver;

  public static MentoringMailMessageBuilder mentoringMessageBuilder() {
    return new MentoringMailMessageBuilder();
  }

}
