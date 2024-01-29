package com.anchor.domain.mentor.api.controller.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequiredMentorEmailInfo {

  private String receiver;
  private String userEmailCode;
}
