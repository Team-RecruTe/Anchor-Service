package com.anchor.domain.notification.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReceiverType {

  TO_MENTOR("/mentors"),
  TO_MENTEE("/users");

  private final String path;

}