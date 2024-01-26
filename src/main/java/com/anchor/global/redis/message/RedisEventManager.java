package com.anchor.global.redis.message;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedisEventManager<T> {

  private final RedisOperations<String, T> redis;
  private final RedisMessageListenerContainer redisContainer;

  public void convertAndSend(String id, String notification) {
    System.out.println("ChannelName: " + getChannelName(id));
    redis.convertAndSend(getChannelName(id), notification);
  }

  public void addMessageListener(String id, MessageListener messageListener) {
    this.redisContainer.addMessageListener(messageListener, getTopic(id));
  }

  private ChannelTopic getTopic(String id) {
    System.out.println("ChannelTopic: " + ChannelTopic.of(getChannelName(id)));
    return ChannelTopic.of(getChannelName(id));
  }

  private String getChannelName(String id) {
    return "topics:" + id;
  }

  public void removeMessageListener(MessageListener messageListener) {
    redisContainer.removeMessageListener(messageListener);
  }

}
