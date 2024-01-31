package com.anchor.domain.mentoring.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.anchor.domain.image.api.controller.ImageController;
import com.anchor.domain.image.api.service.ImageService;
import com.anchor.domain.mentoring.api.controller.request.MentoringBasicInfo;
import com.anchor.domain.mentoring.api.service.MentoringService;
import com.anchor.domain.mentoring.api.service.response.MentoringCreateResult;
import com.anchor.global.auth.SessionUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@MockBean(JpaMetamodelMappingContext.class)
@WithMockUser(username = "hossi", roles = {"MENTOR"})
@WebMvcTest({MentoringController.class, ImageController.class})
class MentoringControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  MentoringService mentoringService;

  @MockBean
  ImageService imageService;

  @MockBean
  SessionUser sessionUser;

  @DisplayName("멘토링 필수 정보 중에 잘못된 {durationTime}에 대한 {400, BadRequest}를 응답합니다.")
  @Test
  void failToValidateDurationTime() throws Exception {
    // given
    MockHttpSession session = new MockHttpSession();
    SessionUser user = new SessionUser();
    user.addMentorId(1L);
    session.setAttribute("user", user);

    BDDMockito.given(mentoringService.create(any(Long.class), any(MentoringBasicInfo.class)))
        .willReturn(new MentoringCreateResult(1L));

    MentoringBasicInfo mentoringBasicInfo = MentoringBasicInfo.builder()
        .title("제목입니다")
        .durationTime("10m 1h")
        .cost(10000)
        .build();

    String json = objectMapper.writeValueAsString(mentoringBasicInfo);

    // when
    ResultActions perform = mockMvc.perform(post("/mentorings")
        .with(csrf())
        .session(session)
        .contentType("application/json")
        .content(json));

    // then
    perform.andDo(print())
        .andExpect(status().isBadRequest());
  }

  @DisplayName("멘토링 필수 정보 중에 올바른 {durationTime}에 대해 {200, OK}를 응답합니다.")
  @Test
  void succeedToValidateDurationTime() throws Exception {
    // given
    MockHttpSession session = new MockHttpSession();
    SessionUser user = new SessionUser();
    user.addMentorId(1L);
    session.setAttribute("user", user);

    BDDMockito.given(sessionUser.getMentorId())
        .willReturn(1L);

    BDDMockito.given(mentoringService.create(any(Long.class),
            any(MentoringBasicInfo.class)))
        .willReturn(new MentoringCreateResult(1L));

    MentoringBasicInfo mentoringBasicInfo = MentoringBasicInfo.builder()
        .title("제목입니다.")
        .durationTime("1h 20m")
        .cost(10000)
        .build();

    String json = objectMapper.writeValueAsString(mentoringBasicInfo);

    // when
    ResultActions perform = mockMvc.perform(post("/mentorings")
        .with(csrf())
        .session(session)
        .contentType("application/json")
        .content(json));

    // then
    perform.andDo(print())
        .andExpect(status().isOk());
  }

  @DisplayName("비어있는 이미지에 대해 Validation을 실패합니다.")
  @Test
  void registerUnavailableTimes() throws Exception {
    // given
    MockHttpSession session = new MockHttpSession();
    session.setAttribute("user", new SessionUser());
    byte[] image = new byte[0];

    MockMultipartFile file = new MockMultipartFile("image", "image.png", MediaType.IMAGE_PNG_VALUE,
        new ByteArrayInputStream(image));

    // when
    ResultActions perform = mockMvc.perform(
        multipart(POST, "/image/upload")
            .file(file)
            .with(csrf())
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .session(session));

    // then
    perform.andDo(print())
        .andExpect(status().isBadRequest());
  }

}