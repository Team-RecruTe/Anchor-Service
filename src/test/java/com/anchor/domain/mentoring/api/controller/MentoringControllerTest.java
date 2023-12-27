package com.anchor.domain.mentoring.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentoring.api.controller.request.MentoringBasicInfo;
import com.anchor.domain.mentoring.api.controller.request.MentoringUnavailableTimeInfos.DateTimeRange;
import com.anchor.domain.mentoring.api.service.MentoringService;
import com.anchor.domain.mentoring.api.service.response.MentoringCreationResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@MockBean(JpaMetamodelMappingContext.class)
@WithMockUser(username = "hossi", roles = {"MENTOR"})
@WebMvcTest(MentoringController.class)
class MentoringControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  MentoringService mentoringService;

  @DisplayName("멘토링 필수 정보를 입력받아, title 검증을 거치고, 실패합니다.")
  @Test
  void validateTitle() throws Exception {
    // given
    Mockito.when(
            mentoringService.create(any(Mentor.class),
                any(MentoringBasicInfo.class)))
        .thenReturn(new MentoringCreationResult(1L));

    MentoringBasicInfo mentoringBasicInfo = MentoringBasicInfo.builder()
        .title("  ")
        .durationTime("10m")
        .cost(10000)
        .build();

    String json = objectMapper.writeValueAsString(mentoringBasicInfo);

    // when
    ResultActions perform = mockMvc.perform(post("/mentorings")
        .with(csrf())
        .contentType("application/json")
        .content(json));

    // then
    perform.andDo(print())
        .andExpect(status().isBadRequest());
  }

  @DisplayName("멘토링 필수 정보를 입력받아, durationTime 검증을 거치고, 실패합니다.")
  @Test
  void validateDurationTime() throws Exception {
    // given
    Mockito.when(
            mentoringService.create(any(Mentor.class),
                any(MentoringBasicInfo.class)))
        .thenReturn(new MentoringCreationResult(1L));

    MentoringBasicInfo mentoringBasicInfo = MentoringBasicInfo.builder()
        .title("제목입니다")
        .durationTime("10m 1h")
        .cost(10000)
        .build();

    String json = objectMapper.writeValueAsString(mentoringBasicInfo);

    // when
    ResultActions perform = mockMvc.perform(post("/mentorings")
        .with(csrf())
        .contentType("application/json")
        .content(json));

    // then
    perform.andDo(print())
        .andExpect(status().isBadRequest());
  }

  @DisplayName("멘토링 필수 정보를 입력받아, cost 검증을 거치고, 실패합니다.")
  @Test
  void validateCost() throws Exception {
    // given
    Mockito.when(
            mentoringService.create(any(Mentor.class),
                any(MentoringBasicInfo.class)))
        .thenReturn(new MentoringCreationResult(1L));

    MentoringBasicInfo mentoringBasicInfo = MentoringBasicInfo.builder()
        .title("제목입니다.")
        .durationTime("10m")
        .cost(null)
        .build();

    String json = objectMapper.writeValueAsString(mentoringBasicInfo);

    // when
    ResultActions perform = mockMvc.perform(post("/mentorings")
        .with(csrf())
        .contentType("application/json")
        .content(json));

    // then
    perform.andDo(print())
        .andExpect(status().isBadRequest());
  }

  @DisplayName("멘토링 불가능한 시간을 입력받아, 검증을 마치고, ok를 응답합니다.")
  @Test
  void validateMentoringUnavailableTimeInfos() throws Exception {
    // given
    Map<String, List<DateTimeRange>> mentoringUnavailableTimeInfos = new HashMap<>();
    List<DateTimeRange> dateTimeRanges = List.of(
        DateTimeRange.of(
            LocalDateTime.of(2023, 12, 12, 20, 30),
            LocalDateTime.of(2023, 12, 12, 21, 30)
        ),
        DateTimeRange.of(
            LocalDateTime.of(2023, 12, 12, 21, 30),
            LocalDateTime.of(2023, 12, 12, 22, 30)
        ),
        DateTimeRange.of(
            LocalDateTime.of(2023, 12, 12, 22, 30),
            LocalDateTime.of(2023, 12, 12, 23, 30)
        )
    );
    mentoringUnavailableTimeInfos.put("dateTimeRanges", dateTimeRanges);

    Mockito.doNothing()
        .when(mentoringService)
        .setUnavailableTimes(any(List.class));

    String json = objectMapper.writeValueAsString(mentoringUnavailableTimeInfos);

    // when
    ResultActions perform = mockMvc.perform(post("/mentorings/1/schedule")
        .with(csrf())
        .contentType("application/json")
        .content(json));

    // then
    perform.andDo(print())
        .andExpect(status().isOk());
  }


}