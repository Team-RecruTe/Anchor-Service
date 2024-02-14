package com.anchor.global.redis.client;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.global.exception.type.redis.ReservationTimeExpiredException;
import com.anchor.global.util.type.DateTimeRange;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationLockClient implements RedisClient<ReservationTimeInfo> {

  private final RedisOperations<String, ReservationTimeInfo> redis;

  private final Duration expiredTime = Duration.ofMinutes(5L);

  public static String createKey(Long mentorId, DateTimeRange reservedTime) {
    String reservationLockTime = reservedTime.formattingFromTime();
    return "mentor:" + mentorId + ":" + reservationLockTime;
  }

  public static String createSessionKey(Long id, String email) {
    return "mentoring:" + id + ":" + email;
  }

  public static String createMatchPattern(Mentor mentor) {
    return "mentor:" + mentor.getId() + ":*";
  }

  @Override
  public void save(String key, ReservationTimeInfo value) {
    redis.opsForValue()
        .set(key, value, expiredTime);
  }

  @Override
  public List<ReservationTimeInfo> findAllByKeyword(String pattern) {
    List<ReservationTimeInfo> reservationTimeInfos = new ArrayList<>();
    ScanOptions options = ScanOptions.scanOptions()
        .match(pattern)
        .build();
    try (Cursor<String> scan = redis.scan(options)) {
      while (scan.hasNext()) {
        String key = scan.next();
        ReservationTimeInfo reservationTimeInfo = findByKey(key);
        if (Objects.nonNull(reservationTimeInfo)) {
          reservationTimeInfos.add(reservationTimeInfo);
        }
      }
    }
    return reservationTimeInfos;
  }

  @Override
  public ReservationTimeInfo findByKey(String key) {
    return redis.opsForValue()
        .get(key);
  }

  @Override
  public void remove(String key) {
    redis.delete(key);
  }

  public void refresh(String key, String email) {
    ReservationTimeInfo reservationTimeInfo = Objects.requireNonNull(findByKey(key), () -> {
      throw new ReservationTimeExpiredException();
    });
    if (reservationTimeInfo.isOwner(email)) {
      save(key, reservationTimeInfo);
    }
  }

}