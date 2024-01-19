package com.anchor.global.redis;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageSessionClient implements RedisClient<String> {

  private final RedisTemplate<String, String> redisTemplate;

  @Override
  public void save(String key, String value) {
    redisTemplate.opsForValue()
        .set(key, value);
  }

  @Override
  public List<String> findAllByKeyword(String pattern) {
    return null;
  }

  @Override
  public String findByKey(String key) {
    return null;
  }

  @Override
  public void remove(String key) {

  }

  @Override
  public void refresh(String key) {

  }


}
