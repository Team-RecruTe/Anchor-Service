package com.anchor.global.redis.message;

import com.anchor.domain.mentoring.domain.MentoringStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationEvent {

  private String email;
  private Long mentoringId;
  private String title;
  private MentoringStatus mentoringStatus;
  private ReceiverType receiverType;

  @Builder
  private NotificationEvent(String email, Long mentoringId, String title, MentoringStatus mentoringStatus,
      ReceiverType receiverType) {
    this.email = email;
    this.mentoringId = mentoringId;
    this.title = title;
    this.mentoringStatus = mentoringStatus;
    this.receiverType = receiverType;
  }

  public String createMessage() {
    if (receiverType.equals(ReceiverType.TO_MENTOR)) {
      return String.format("%s: 멘토님께서 멘토링을 %s하셨습니다.", title, mentoringStatus.getDescription());
    }
    return String.format("%s: 멘티님께서 멘토링을 %s하셨습니다.", title, mentoringStatus.getDescription());
  }

}
