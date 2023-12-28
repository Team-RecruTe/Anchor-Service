package com.anchor.domain.mentoring.api.service;

import com.anchor.domain.mentor.domain.Career;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentor.domain.repository.MentorRepository;
import com.anchor.domain.mentoring.api.controller.request.MentoringBasicInfo;
import com.anchor.domain.mentoring.api.controller.request.MentoringContentsInfo;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.repository.MentoringRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

@WithMockUser(username = "hossi", roles = {"USER, MENTOR"})
@SpringBootTest
public class MentoringTest {

  @Autowired
  MentoringService mentoringService;

  @Autowired
  MentoringRepository mentoringRepository;

  @Autowired
  MentorRepository mentorRepository;

  @DisplayName("멘토링 아이디를 입력받아, 멘토링 상세 정보를 등록하고, 아무것도 반환하지 않습니다.")
  @Test
  void registerMentoringDetail() {
    // given
    Mentor mentor = Mentor.builder()
        .accountName("이지훈")
        .career(Career.JUNIOR)
        .accountNumber("123456")
        .companyEmail("asd@naver.com")
        .bankName("한국은행")
        .build();

    MentoringBasicInfo mentoringBasicInfo = MentoringBasicInfo.builder()
        .title("제목입니다!")
        .durationTime("2h")
        .cost(14000)
        .build();
    mentorRepository.save(mentor);

    Mentoring mentoring = Mentoring.builder()
        .title("제목입니다.")
        .durationTime("1h")
        .cost(10000)
        .mentor(mentor)
        .build();
    mentoringRepository.save(mentoring);

    // when
    mentoringService.registerContents(1L, new MentoringContentsInfo("<h1>컨텐츠입니다.<h1>"));

    // then
  }


}
