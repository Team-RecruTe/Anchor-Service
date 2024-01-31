package com.anchor.domain.mentor.api.controller;

import com.anchor.domain.mentor.api.service.MentorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

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

//  @DisplayName("중복된 멘토링 불가능 시간에 대해 Validation을 실패합니다.")
//  @Test
//  void registerUnavailableTimes() throws Exception {
//    // given
//    MockHttpSession session = new MockHttpSession();
//    session.setAttribute("user", new SessionUser());
//
//    List<DateTimeRange> dateTimeRanges = List.of(
//        DateTimeRange.of(
//            LocalDateTime.of(2023, 12, 12, 20, 30, 1),
//            LocalDateTime.of(2023, 12, 12, 21, 30, 1)
//        ),
//        DateTimeRange.of(
//            LocalDateTime.of(2023, 12, 12, 20, 30, 1),
//            LocalDateTime.of(2023, 12, 12, 21, 30, 1)
//        )
//    );
//
//    // when
//    ArrayNode dateTimeRangesJson = objectMapper.createArrayNode();
//    for (DateTimeRange range : dateTimeRanges) {
//      ObjectNode rangeJson = objectMapper.createObjectNode();
//      rangeJson.put("from", range.getFrom()
//          .toString());
//      rangeJson.put("to", range.getTo()
//          .toString());
//      dateTimeRangesJson.add(rangeJson);
//    }
//
//    // Wrap the array in an object with the desired key
//    ObjectNode requestBody = objectMapper.createObjectNode();
//    requestBody.set("dateTimeRanges", dateTimeRangesJson);
//
//    String json = objectMapper.writeValueAsString(dateTimeRanges);
//
//    ResultActions perform = mockMvc.perform(post("/mentors/me/schedule")
//        .with(csrf())
//        .session(session)
//        .contentType("application/json")
//        .content(requestBody.toString()));
////        .content(json));
//
//    // then
//    perform.andDo(print())
//        .andExpect(status().isBadRequest());
//  }

}