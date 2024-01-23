package com.anchor.global.redis.message;

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
