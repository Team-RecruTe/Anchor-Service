package com.anchor.global.redis.message;

import com.anchor.global.util.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "notification", indexes = {
    @Index(name = "notification_read_index", columnList = "is_read")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Notification extends BaseEntity implements Serializable {

  private Long mentoringId;

  private String receiverEmail;

  private String message;

  @Enumerated(EnumType.STRING)
  private ReceiverType receiverType;

  private boolean isRead = false;

  public Notification(String receiverEmail, Long mentoringId, String message, ReceiverType receiverType) {
    this.receiverEmail = receiverEmail;
    this.mentoringId = mentoringId;
    this.message = message;
    this.receiverType = receiverType;
  }

  public static Notification of(NotificationEvent event) {
    return new Notification(
        event.getEmail(),
        event.getMentoringId(),
        event.createMessage(),
        event.getReceiverType()
    );
  }

  public void read() {
    this.isRead = true;
  }

}