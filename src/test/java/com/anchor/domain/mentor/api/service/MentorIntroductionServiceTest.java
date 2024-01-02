package com.anchor.domain.mentor.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.anchor.domain.mentor.api.controller.request.MentorInfoRequest;
import com.anchor.domain.mentor.api.controller.request.MentorIntroductionRequest;
import com.anchor.domain.mentor.api.service.response.MentorInfoResponse;
import com.anchor.domain.mentor.api.service.response.MentorIntroductionResponse;
import com.anchor.domain.mentor.domain.Career;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentor.domain.MentorIntroduction;
import com.anchor.domain.mentor.domain.repository.MentorInfoRepository;
import com.anchor.domain.mentor.domain.repository.MentorIntroductionRepository;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MentorIntroductionServiceTest {

  @InjectMocks
  MentorInfoService mentorInfoService;

  @InjectMocks
  MentorIntroductionService mentorIntroductionService;

  @Mock
  MentorInfoRepository mentorInfoRepository;

  @Mock
  MentorIntroductionRepository mentorIntroductionRepository;

  @BeforeAll
  static void beforeAll(){
    System.out.println("Start Test");
  }

  @AfterAll
  static void afterAll(){
    System.out.println("End Test");
  }
  // 테스트에 사용될 Mentor 객체를 생성하여 반환한다.
  private Mentor buildMentor() {
    return Mentor.builder()
        .companyEmail("mentor@example.com")
        .career(Career.JUNIOR)
        .accountNumber("1234567890")
        .bankName("XX은행")
        .accountName("지선")
        .build();
  }


  @DisplayName("멘토 소개 등록 - 성공")
  @Test
  void registerMentorIntroduction_Success() {
    /*멘토 정보 찾기*/
    // Given
    Long mentorId = 1L;
    MentorInfoRequest requestInfo = new MentorInfoRequest();
    Mentor mentor = buildMentor();

    when(mentorInfoRepository.findById(mentorId)).thenReturn(Optional.of(mentor));
    when(mentorInfoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    // When :
    MentorInfoResponse mentorInfoResponse = mentorInfoService.findMentors(mentorId);

    /*멘토 소개 등록*/
    // Given
    MentorIntroductionRequest requestIntroduction = new MentorIntroductionRequest("Mentor introduction content");
    when(mentorIntroductionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    //MentorIntroductionResponse mentorIntroductionResponse = mentorIntroductionService.findMentors(mentorId); // 수정된 부분

    // Then
    verify(mentorIntroductionRepository, times(1)).save(any());
  }

  @DisplayName("멘토 소개 등록 - 실패 (내용이 null)")
  @Test
  void registerMentorIntroduction_Fail_NullContents() {
    // Given
    MentorIntroductionRequest request = new MentorIntroductionRequest(null);

    // When, Then
    assertThrows(IllegalArgumentException.class, () -> mentorIntroductionService.findMentors(1L));
    verify(mentorIntroductionRepository, never()).save(any());
  }

  @DisplayName("멘토 소개 수정 - 성공")
  @Test
  void editMentorIntroduction_Success() {
    // Given
    MentorIntroductionRequest request = new MentorIntroductionRequest("Updated mentor introduction content");
    MentorIntroduction existingIntroduction = MentorIntroduction.registerDetail("Original content");

    when(mentorIntroductionRepository.findById(1L)).thenReturn(java.util.Optional.of(existingIntroduction));

    // When
    mentorIntroductionService.editContents(1L, request);

    // Then
    verify(mentorIntroductionRepository, times(1)).save(any());
  }

  @DisplayName("멘토 소개 수정 - 실패 (존재하지 않는 ID)")
  @Test
  void editMentorIntroduction_Fail_InvalidId() {
    // Given
    MentorIntroductionRequest request = new MentorIntroductionRequest("Updated mentor introduction content");

    // When, Then
    assertThrows(IllegalArgumentException.class, () -> mentorIntroductionService.editContents(1L, request));
    verify(mentorIntroductionRepository, never()).save(any());
  }

  @DisplayName("멘토 소개 수정 - 실패 (내용이 null)")
  @Test
  void editMentorIntroduction_Fail_NullContents() {
    // Given
    MentorIntroductionRequest request = new MentorIntroductionRequest(null);
    MentorIntroduction existingIntroduction = MentorIntroduction.registerDetail("Original content");

    when(mentorIntroductionRepository.findById(1L)).thenReturn(java.util.Optional.of(existingIntroduction));

    // When, Then
    assertThrows(IllegalArgumentException.class, () -> mentorIntroductionService.editContents(1L, request));
    verify(mentorIntroductionRepository, never()).save(any());
  }


  @DisplayName("멘토 소개 예외처리 테스트")
  @Test
  void exceptionHandlingTest() {
    // Given
    when(mentorIntroductionRepository.findById(1L)).thenReturn(Optional.empty());

    // When, Then
    assertThrows(RuntimeException.class, () -> mentorIntroductionService.findMentors(1L));
  }




}