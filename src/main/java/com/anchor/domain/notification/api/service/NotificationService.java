package com.anchor.domain.notification.api.service;

import com.anchor.domain.notification.api.service.response.AllNotification;
import com.anchor.domain.notification.api.service.response.NotificationResponse;
import com.anchor.domain.notification.domain.Notification;
import com.anchor.domain.notification.domain.repository.NotificationRepository;
import com.anchor.global.exception.type.entity.NotificationNotFoundException;
import com.anchor.global.redis.message.MentoringNotification;
import com.anchor.global.redis.message.NotificationEvent;
import com.anchor.global.redis.message.RedisEventManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/*
 * - "SseEmitter": Spring 에서 SSE 프로토콜을 지원하기 위한 클래스
 * - 실시간으로 업데이트되는 이벤트성 데이터를 클라이언트에게 전달 가능
 * - 비동기적으로 이벤트 전송 가능
 * - 클라이언트와의 연결이 끊어지는 경우에도 재시도 지원
 * - 여러 클라이언트와 동시에 통신이 가능
 */

/*
 * @TransactionalEventListener: 트랜잭션의 어떤 타이밍에 이벤트를 발생시킬 지 정할 수 있습니다.
 * - AFTER_COMMIT (기본값): 트랜잭션이 성공적으로 마무리(commit)됬을 때 이벤트 실행
 * - AFTER_ROLLBACK: 트랜잭션이 rollback 됬을 때 이벤트 실행
 * - AFTER_COMPLETION: 트랜잭션이 마무리 됬을 때(commit or rollback) 이벤트 실행
 * - BEFORE_COMMIT: 트랜잭션의 커밋 전에 이벤트 실행
 */

@RequiredArgsConstructor
@Service
public class NotificationService {

  private static final Logger log = LoggerFactory.getLogger(Notification.class);
  private static final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
  private static final long DEFAULT_TIMEOUT = 60L * 1000 * 5;
  private static final String DEFAULT_MESSAGE = "EventStream Created.";
  private final RedisEventManager<MentoringNotification> redisEventManager;
  private final NotificationRepository notificationRepository;
  private final ObjectMapper objectMapper;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener
  public void send(NotificationEvent event) {
    Notification notification = Notification.of(event);
    notificationRepository.save(notification);
    String id = String.valueOf(notification.getReceiverEmail());
    redisEventManager.convertAndSend(id, serialize(notification));
  }

  public SseEmitter subscribe(String email) throws IOException {
    String id = String.valueOf(email);
    SseEmitter emitter = createAndSetEmitter(id);
    NotificationResponse response = new MentoringNotification();
    MessageListener messageListener = createMessageListener(id, emitter, response);
    redisEventManager.addMessageListener(id, messageListener);
    emitter.onCompletion(removeEmitter(emitter, messageListener));
    emitter.onTimeout(removeEmitter(emitter, messageListener));
    log.info("ID: {}", id);
    return emitter;
  }

  private MessageListener createMessageListener(String id, SseEmitter emitter, NotificationResponse response) {
    return (message, pattern) -> {
      Notification notification = deserialize(message);
      response.setFrom(notification);
      try {
        emitter.send(SseEmitter.event()
            .id(id)
            .name("sse")
            .data(response));
      } catch (IOException e) {
        emitters.remove(emitter);
      }
    };
  }

  private String serialize(Notification notification) {
    try {
      return this.objectMapper.writeValueAsString(notification);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Notification deserialize(Message message) {
    try {
      return this.objectMapper.readValue(message.getBody(), Notification.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private SseEmitter createAndSetEmitter(String id) throws IOException {
    SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
    emitter.send(SseEmitter.event()
        .id(id)
        .name("sse")
        .data(DEFAULT_MESSAGE));
    emitters.add(emitter);
    return emitter;
  }

  private Runnable removeEmitter(SseEmitter emitter, final MessageListener messageListener) {
    return () -> {
      emitters.remove(emitter);
      redisEventManager.removeMessageListener(messageListener);
    };
  }

  @Transactional(readOnly = true)
  public Long getCount(String email) {
    return notificationRepository.countByReceiverEmailAndIsRead(email, false);
  }

  @Transactional(readOnly = true)
  public AllNotification getAllNotifications(String email) {
    List<Notification> notifications = notificationRepository.findByReceiverEmail(email);
    return new AllNotification(notifications);
  }

  @Transactional
  public void readNotification(Long id) {
    Notification notification = notificationRepository.findById(id)
        .orElseThrow(NotificationNotFoundException::new);
    notification.read();
    notificationRepository.save(notification);
  }

  public void readAllNotifications(String email) {
    List<Notification> notifications = notificationRepository.findByReceiverEmailAndIsRead(email, true);
    notifications.forEach(Notification::read);
    notificationRepository.saveAll(notifications);
  }

}
