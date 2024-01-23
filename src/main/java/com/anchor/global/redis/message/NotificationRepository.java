package com.anchor.global.redis.message;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

  long countByReceiverEmailAndIsRead(String email, boolean isRead);

  List<Notification> findByReceiverEmail(String email);

}
