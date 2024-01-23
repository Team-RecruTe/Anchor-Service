package com.anchor.global.redis;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageSessionClient implements RedisClient<String> {

  private final RedisOperations<String, String> redis;

  @Override
  public void save(String key, String value) {
    redis.opsForValue()
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
