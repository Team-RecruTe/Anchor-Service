package com.anchor.global.redis.lock;

import com.anchor.domain.mentoring.domain.Mentoring;
import groovy.util.logging.Slf4j;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisLockFacade {

  private final MentoringServiceWithLock mentoringServiceWithLock;
  private final RedissonClient redissonClient;

  public Mentoring increaseTotalApplication(Long mentoringId) {
    RLock lock = redissonClient.getLock(mentoringId.toString());

    try {
      boolean available = lock.tryLock(1, 1, TimeUnit.SECONDS);
      if (!available) {
        throw new RuntimeException("lock 획득에 실패했습니다.");
      }
      return mentoringServiceWithLock.increaseTotalApplication(mentoringId);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      lock.unlock();
    }

  }

}

