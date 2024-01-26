package com.anchor.domain.notification.api.controller;

import com.anchor.domain.notification.api.service.NotificationService;
import com.anchor.domain.notification.api.service.response.AllNotification;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@RestController
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping("/count")
  public ResponseEntity<Long> count(HttpSession httpSession) throws IOException {
//    SessionUser user = (SessionUser) httpSession.getAttribute("user");
    Long count = notificationService.getCount("testUser@test.com");
    return ResponseEntity.ok(count);
  }

  @GetMapping(value = "/connect", produces = "text/event-stream")
  public SseEmitter subscribe(HttpSession httpSession) throws IOException {
//    SessionUser user = (SessionUser) httpSession.getAttribute("user");
    return notificationService.subscribe("testUser@test.com");
  }

  @GetMapping
  public ResponseEntity<AllNotification> notifications(HttpSession httpSession) {
//    SessionUser user = (SessionUser) httpSession.getAttribute("user");
    AllNotification notifications = notificationService.getAllNotifications("testUser@test.com");
    return ResponseEntity.ok(notifications);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Void> readNotification(@PathVariable Long id, HttpSession httpSession) {
//    SessionUser user = (SessionUser) httpSession.getAttribute("user");
    notificationService.readNotification(id);
    return ResponseEntity.noContent()
        .build();
  }

  @PutMapping("/read-all")
  public ResponseEntity<Void> readAllNotifications(HttpSession httpSession) {
//    SessionUser user = (SessionUser) httpSession.getAttribute("user");
    notificationService.readAllNotifications("testUser@test.com");
    return ResponseEntity.noContent()
        .build();
  }

}