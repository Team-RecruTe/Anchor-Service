package com.anchor.global.redis;

import java.util.List;

public interface RedisClient<V> {

  void save(String key, V value);

  List<V> findAllByKeyword(String pattern);

  V findByKey(String key);

  void remove(String key);

  void refresh(String key);

}
