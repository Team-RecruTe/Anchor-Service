package com.anchor.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession
@EnableRedisRepositories
public class RedisConfig {


  @Value("${spring.data.redis.session.host}")
  private String hostName;

  @Value("${spring.data.redis.session.port}")
  private int sessionPort;

  @Value("${spring.data.redis.storage.port}")
  private int storagePort;

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
    redisStandaloneConfiguration.setHostName(hostName);
    redisStandaloneConfiguration.setPort(sessionPort);
    return new LettuceConnectionFactory(redisStandaloneConfiguration);
  }

  @Bean(name = "redisStorageConnectionFactory")
  public RedisConnectionFactory redisStorageConnectionFactory() {
    RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
    redisStandaloneConfiguration.setHostName(hostName);
    redisStandaloneConfiguration.setPort(storagePort);
    return new LettuceConnectionFactory(redisStandaloneConfiguration);
  }

  @Bean
  public RedisTemplate<String, ?> redisTemplate() {
    RedisTemplate<String, ?> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisStorageConnectionFactory());
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    return redisTemplate;
  }

}
