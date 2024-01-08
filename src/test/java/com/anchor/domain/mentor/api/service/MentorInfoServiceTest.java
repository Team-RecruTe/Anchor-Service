package com.anchor.domain.mentor.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import com.anchor.domain.mentor.api.controller.request.MentorInfoRequest;
import com.anchor.domain.mentor.api.service.response.MentorInfoResponse;
import com.anchor.domain.mentor.domain.Career;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentor.domain.MentorIntroduction;
import com.anchor.domain.mentor.domain.repository.MentorInfoRepository;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(MockitoExtension.class)
class MentorInfoServiceTest {

  @InjectMocks
  MentorInfoService mentorInfoService;

  @Mock
  MentorInfoRepository mentorInfoRepository;

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


  @DisplayName("멘토 정보 조회")
  @Test
  void findMentors(){
    // Given
    Long mentorId = 1L;
    MentorInfoRequest request = new MentorInfoRequest();
    Mentor mentor = buildMentor();

    when(mentorInfoRepository.findById(mentorId)).thenReturn(Optional.of(mentor));

    // When : 아래 코드를 실행하고
    // mentorInfoService.findMentors(mentorId) 메서드를 호출하고, 이 메서드는 mentorInfoRepository.findById(mentorId)를 호출한다.
    MentorInfoResponse mentorInfoResponse = mentorInfoService.findMentor(mentorId);

    // Then : 위의 결과와 실제 Mentor 객체의 정보를 비교
    assertEquals(mentor.getCompanyEmail(), mentorInfoResponse.getCompanyEmail());
    assertEquals(mentor.getCareer(), mentorInfoResponse.getCareer());
    assertEquals(mentor.getBankName(), mentorInfoResponse.getBankName());
    assertEquals(mentor.getAccountNumber(), mentorInfoResponse.getAccountNumber());
    //assertEquals(mentor.getMentorIntroduction(), mentorInfoResponse.getMentorIntroduction());

    // Verify : that findById method was called once with the correct argument
    verify(mentorInfoRepository, times(1)).findById(mentorId);

  }


  @DisplayName("멘토 필수정보 수정")
  @Test
  void modifyMentorsInfo() {
    // Given
    Long mentorId = 1L;
    MentorInfoRequest request = MentorInfoRequest.builder()
        .career(Career.SENIOR)
        .bankName("새로운은행")
        .accountNumber("9876543210")
        .accountName("새로운이름")
        .build();

    Mentor originMentor = buildMentor();

    // findById 및 save 메서드의 동작을 목 객체(Mock)로 설정
    when(mentorInfoRepository.findById(mentorId)).thenReturn(Optional.of(originMentor));
    when(mentorInfoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    mentorInfoService.editInfo(mentorId, request);

    // Then
    // Repository의 save 메서드가 한 번 호출되었는지 확인
    verify(mentorInfoRepository, times(1)).save(any());

  }


  @DisplayName("멘토 삭제")
  @Test
  void deleteMentors() {
    // Given
    Long mentorId = 1L;

    // When
    mentorInfoService.deleteMentors(mentorId);

    // Then
    // Repository의 deleteById 메서드가 한 번 호출되었는지 확인
    verify(mentorInfoRepository, times(1)).deleteById(mentorId);
  }



}