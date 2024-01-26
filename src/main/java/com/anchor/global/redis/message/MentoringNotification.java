package com.anchor.global.redis.message;

import com.anchor.domain.notification.api.service.response.NotificationResponse;
import com.anchor.domain.notification.domain.Notification;
import com.anchor.global.util.ResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentoringNotification extends ResponseDto implements NotificationResponse {

  private Long NotificationId;
  private Long mentoringId;
  private String message;

  private MentoringNotification(Long NotificationId, Long mentoringId, String message) {
    this.NotificationId = NotificationId;
    this.mentoringId = mentoringId;
    this.message = message;
  }

  public static MentoringNotification of(Notification notification) {
    return new MentoringNotification(notification.getId(), notification.getMentoringId(), notification.getMessage());
  }

  public void setFrom(Notification notification) {
    this.NotificationId = notification.getMentoringId();
    this.mentoringId = notification.getMentoringId();
    this.message = notification.getMessage();
  }

}


