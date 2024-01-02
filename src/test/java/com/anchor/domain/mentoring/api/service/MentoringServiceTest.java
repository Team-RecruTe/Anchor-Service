package com.anchor.domain.mentoring.api.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;

import com.anchor.domain.mentor.domain.Career;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentoring.api.controller.request.MentoringBasicInfo;
import com.anchor.domain.mentoring.api.service.response.MentoringCreateResult;
import com.anchor.domain.mentoring.api.service.response.MentoringEditResult;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.repository.MentoringRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MentoringServiceTest {

  @InjectMocks
  MentoringService mentoringService;

  @Mock
  private MentoringRepository mentoringRepository;

  @DisplayName("멘토링 필수정보를 입력받아, 멘토링을 생성하고, 멘토링 아이디를 반환합니다.")
  @Test
  void create() {
    // given
    Mentor mentor = Mentor.builder()
        .accountName("이지훈")
        .career(Career.JUNIOR)
        .accountNumber("123456")
        .companyEmail("asd@naver.com")
        .bankName("한국은행")
        .build();

    MentoringBasicInfo mentoringBasicInfo = MentoringBasicInfo.builder()
        .title("제목입니다.")
        .durationTime("1h")
        .cost(10000)
        .build();

    Mentoring mentoring = Mentoring.builder()
        .title(mentoringBasicInfo.getTitle())
        .durationTime(mentoringBasicInfo.getDurationTime())
        .cost(mentoringBasicInfo.getCost())
        .mentor(mentor)
        .build();

    when(mentoringRepository.save(any())).thenReturn(mentoring);

    // when
    MentoringCreateResult mentoringCreateResult = mentoringService.create(mentor,
        mentoringBasicInfo);

    // then
    assertThat(mentoringCreateResult).isNotNull();
    assertThat(mentoringCreateResult).extracting("mentoringId")
        .isEqualTo(1L);
  }

  @DisplayName("멘토링 필수정보를 입력받아, 멘토링을 수정하고, 멘토링 아이디를 반환합니다.")
  @Test
  void edit() {
    // given
    Mentor savedMentor = Mentor.builder()
        .accountName("이지훈")
        .career(Career.JUNIOR)
        .accountNumber("123456")
        .companyEmail("asd@naver.com")
        .bankName("한국은행")
        .build();

    Mentoring savedMentoring = Mentoring.builder()
        .title("제목입니다.")
        .durationTime("1h")
        .cost(10000)
        .mentor(savedMentor)
        .build();

    MentoringBasicInfo mentoringBasicInfo = MentoringBasicInfo.builder()
        .title("제목입니다!")
        .durationTime("2h")
        .cost(14000)
        .build();

    Mentoring editedMentoring = Mentoring.builder()
        .title(mentoringBasicInfo.getTitle())
        .durationTime(mentoringBasicInfo.getDurationTime())
        .cost(mentoringBasicInfo.getCost())
        .mentor(savedMentor)
        .build();

    when(mentoringRepository.findById(anyLong())).thenReturn(Optional.ofNullable(savedMentoring));
    when(mentoringRepository.save(any())).thenReturn(editedMentoring);

    // when
    MentoringEditResult mentoringEditResult = mentoringService.edit(1L,
        mentoringBasicInfo);

    // then
    assertThat(mentoringEditResult).isNotNull();
    assertThat(mentoringEditResult).extracting("title", "durationTime", "cost")
        .isEqualTo(
            tuple("제목입니다!", "2h", 14000).toList()
        );
  }

  @DisplayName("멘토링 아이디를 입력받아, 멘토링을 삭제하고, 아무것도 반환하지 않습니다.")
  @Test
  void delete() {
    // given
    Mentor savedMentor = Mentor.builder()
        .accountName("이지훈")
        .career(Career.JUNIOR)
        .accountNumber("123456")
        .companyEmail("asd@naver.com")
        .bankName("한국은행")
        .build();

    Mentoring savedMentoring = Mentoring.builder()
        .title("제목입니다.")
        .durationTime("1h")
        .cost(10000)
        .mentor(savedMentor)
        .build();

    when(mentoringRepository.findById(anyLong())).thenReturn(Optional.ofNullable(savedMentoring));
    doNothing().when(mentoringRepository)
        .delete(any(Mentoring.class));

    // when
    mentoringService.delete(1L);

    // then
    Mockito.verify(mentoringRepository, times(1))
        .delete(any(Mentoring.class));
  }

}