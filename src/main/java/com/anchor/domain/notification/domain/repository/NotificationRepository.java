package com.anchor.domain.notification.domain.repository;

import com.anchor.domain.notification.domain.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

  long countByReceiverEmailAndIsRead(String email, boolean isRead);

  List<Notification> findByReceiverEmail(String email);

  List<Notification> findByReceiverEmailAndIsRead(String email, boolean isRead);

}
