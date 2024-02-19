package com.anchor.domain.notification.api.controller;

import com.anchor.domain.notification.api.service.NotificationService;
import com.anchor.domain.notification.api.service.response.AllNotification;
import com.anchor.global.auth.SessionUser;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@RestController
public class NotificationController {

  private final NotificationService notificationService;

  /**
   * 읽지 않은 알림의 개수를 조회합니다.
   */
  @PreAuthorize("hasRole('ROLE_USER')")
  @GetMapping("/count")
  public ResponseEntity<Long> count(@SessionAttribute("user") SessionUser user) throws IOException {
    Long count = notificationService.getCount(user.getEmail());
    return ResponseEntity.ok(count);
  }

  /**
   * 실시간 알림 이벤트를 구독 처리합니다.
   */
  @PreAuthorize("hasRole('ROLE_USER')")
  @GetMapping(value = "/connect", produces = "text/event-stream")
  public SseEmitter subscribe(@SessionAttribute("user") SessionUser user) throws IOException {
    return notificationService.subscribe(user.getEmail());
  }

  /**
   * 모든 알림을 조회합니다.
   */
  @PreAuthorize("hasRole('ROLE_USER')")
  @GetMapping
  public ResponseEntity<AllNotification> getAllNotifications(@SessionAttribute("user") SessionUser user) {
    AllNotification notifications = notificationService.getAllNotifications(user.getEmail());
    return ResponseEntity.ok(notifications);
  }

  /**
   * 알림을 읽음 처리합니다.
   */
  @PreAuthorize("hasRole('ROLE_USER')")
  @PutMapping("/{id}")
  public ResponseEntity<Void> readNotification(@PathVariable Long id, @SessionAttribute("user") SessionUser user) {
    notificationService.readNotification(id, user.getEmail());
    return ResponseEntity.noContent()
        .build();
  }

  /**
   * 모든 알림을 읽음 처리합니다.
   */
  @PreAuthorize("hasRole('ROLE_USER')")
  @PutMapping("/read-all")
  public ResponseEntity<Void> readAllNotifications(@SessionAttribute("user") SessionUser user) {
    notificationService.readAllNotifications(user.getEmail());
    return ResponseEntity.noContent()
        .build();
  }

}