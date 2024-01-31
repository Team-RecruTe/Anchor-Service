package com.anchor.global.util;

import com.anchor.global.exception.type.json.JsonDeserializationFailedException;
import com.anchor.global.exception.type.json.JsonSerializationFailedException;
import com.anchor.global.util.type.JsonSerializable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JsonUtils {

  private final ObjectMapper objectMapper;

  public <T extends JsonSerializable> String serializeObjectToJson(T object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new JsonSerializationFailedException(e);
    }
  }

  public <T extends JsonSerializable> T convertValue(Object obj, Class<T> clazz) {
    try {
      return objectMapper.convertValue(obj, clazz);
    } catch (IllegalArgumentException e) {
      throw new JsonDeserializationFailedException(e);
    }
  }

  public <T extends JsonSerializable> T deserializejsonToObject(byte[] json, Class<T> clazz) {
    try {
      return objectMapper.readValue(json, clazz);
    } catch (IOException e) {
      throw new JsonDeserializationFailedException(e);
    }
  }

  public <T extends JsonSerializable> T deserializejsonToObject(InputStream inputStream, Class<T> clazz) {
    try {
      return objectMapper.readValue(inputStream, clazz);
    } catch (IOException e) {
      throw new JsonDeserializationFailedException(e);
    }
  }

}
