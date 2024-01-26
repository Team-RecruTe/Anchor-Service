package com.anchor.domain.notification.api.service.response;

import com.anchor.domain.notification.domain.Notification;
import com.anchor.global.redis.message.MentoringNotification;
import com.anchor.global.util.type.Link;
import java.util.List;
import lombok.Getter;

@Getter
public class AllNotification {

  private final List<MentoringNotification> notifications;

  public AllNotification(List<Notification> notifications) {
    this.notifications = notifications.stream()
        .map(notification -> {
          MentoringNotification mentoringNotification = MentoringNotification.of(notification);
          mentoringNotification.addLinks(Link.builder()
              .setLink("self", String.format("/mentorings/%d", mentoringNotification.getMentoringId()))
              .build());
          return mentoringNotification;
        })
        .toList();
  }

}
