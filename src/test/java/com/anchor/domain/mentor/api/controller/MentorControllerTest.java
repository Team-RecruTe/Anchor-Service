package com.anchor.domain.mentor.api.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.anchor.domain.mentor.api.controller.request.PayupMonthRange;
import com.anchor.domain.mentor.api.service.MailService;
import com.anchor.domain.mentor.api.service.MentorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@MockBean(JpaMetamodelMappingContext.class)
@WithMockUser(roles = "MENTOR")
@WebMvcTest(MentorController.class)
class MentorControllerTest {

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  MockMvc mockMvc;

  @MockBean
  MentorService mentorService;

  @MockBean
  MailService mailService;

  @Test
  @DisplayName("정산내역 조회 요청시 이번달보다 미래시점이라면 Validation을 실패합니다.")
  void notFutureMonthTest() throws Exception {
    //given
    PayupMonthRange monthRange = PayupMonthRange.builder()
        .startMonth(LocalDateTime.of(2023, 6, 1, 0, 0, 0))
        .currentMonth(LocalDateTime.now()
            .plusMonths(1L))
        .build();

    //when, then
    mockMvc.perform(MockMvcRequestBuilders.get("/mentors/me/payup-info")
            .param("currentMonth", monthRange.getCurrentMonth()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))
            .param("startMonth", monthRange.getStartMonth()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status()
            .isBadRequest())
        .andExpect(result -> {
          String responseBody = result.getResponse()
              .getContentAsString(StandardCharsets.UTF_8);
          assertThat(responseBody).contains("이번달 보다 미래시점의 조회는 불가능합니다.");
        });
  }

}