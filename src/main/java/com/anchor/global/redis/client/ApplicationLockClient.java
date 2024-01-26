package com.anchor.global.redis.client;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.global.auth.SessionUser;
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
public class ApplicationLockClient implements RedisClient<DateTimeRange> {

  private final RedisOperations<String, DateTimeRange> redis;
  private final Duration expiredTime = Duration.ofMinutes(5L);

  public static String createKey(Mentor mentor, SessionUser sessionUser) {
    return "mentor:" + mentor.getId() + ":" + sessionUser.getEmail();
  }

  public static String createMatchPattern(Mentor mentor) {
    return "mentor:" + mentor.getId() + ":*";
  }

  @Override
  public void save(String key, DateTimeRange value) {
    redis.opsForValue()
        .set(key, value, expiredTime);
  }

  @Override
  public List<DateTimeRange> findAllByKeyword(String pattern) {
    List<DateTimeRange> applicationLockTimes = new ArrayList<>();
    ScanOptions options = ScanOptions.scanOptions()
        .match(pattern)
        .build();
    try (Cursor<String> scan = redis.scan(options)) {
      while (scan.hasNext()) {
        String key = scan.next();
        DateTimeRange dateTimeRange = findByKey(key);
        if (Objects.nonNull(dateTimeRange)) {
          applicationLockTimes.add(dateTimeRange);
        }
      }
    }
    return applicationLockTimes;
  }

  @Override
  public DateTimeRange findByKey(String key) {
    return redis.opsForValue()
        .get(key);
  }

  @Override
  public void remove(String key) {
    redis.delete(key);
  }

  @Override
  public void refresh(String key) {
    DateTimeRange dateTimeRange = findByKey(key);
    if (Objects.nonNull(dateTimeRange)) {
      redis.opsForValue()
          .set(key, dateTimeRange, expiredTime);
    } else {
      throw new RuntimeException("이미 예약시간이 만료되었습니다. 홈페이지로 이동합니다.");
    }
  }
}
