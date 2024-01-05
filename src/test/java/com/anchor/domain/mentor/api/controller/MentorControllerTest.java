package com.anchor.domain.mentor.api.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.anchor.domain.mentor.api.service.MentorService;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.util.type.DateTimeRange;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

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

  @DisplayName("중복된 멘토링 불가능 시간에 대해 Validation을 실패합니다.")
  @Test
  void registerUnavailableTimes() throws Exception {
    // given
    MockHttpSession session = new MockHttpSession();
    session.setAttribute("user", new SessionUser());

    List<DateTimeRange> dateTimeRanges = List.of(
        DateTimeRange.of(
            LocalDateTime.of(2023, 12, 12, 20, 30, 1),
            LocalDateTime.of(2023, 12, 12, 21, 30, 1)
        ),
        DateTimeRange.of(
            LocalDateTime.of(2023, 12, 12, 20, 30, 1),
            LocalDateTime.of(2023, 12, 12, 21, 30, 1)
        )
    );

    // when
    ArrayNode dateTimeRangesJson = objectMapper.createArrayNode();
    for (DateTimeRange range : dateTimeRanges) {
      ObjectNode rangeJson = objectMapper.createObjectNode();
      rangeJson.put("from", range.getFrom()
          .toString());
      rangeJson.put("to", range.getTo()
          .toString());
      dateTimeRangesJson.add(rangeJson);
    }

    // Wrap the array in an object with the desired key
    ObjectNode requestBody = objectMapper.createObjectNode();
    requestBody.set("dateTimeRanges", dateTimeRangesJson);

    String json = objectMapper.writeValueAsString(dateTimeRanges);

    ResultActions perform = mockMvc.perform(post("/mentors/me/schedule")
        .with(csrf())
        .session(session)
        .contentType("application/json")
        .content(requestBody.toString()));
//        .content(json));

    // then
    perform.andDo(print())
        .andExpect(status().isBadRequest());
  }

}