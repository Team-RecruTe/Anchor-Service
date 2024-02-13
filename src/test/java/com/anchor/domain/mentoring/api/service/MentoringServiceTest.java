package com.anchor.domain.mentoring.api.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentoring.api.controller.request.MentoringApplicationUserInfo;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.repository.MentoringRepository;
import com.anchor.global.exception.type.redis.ReservationTimeExpiredException;
import com.anchor.global.redis.client.ApplicationLockClient;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class MentoringServiceTest {

  @Mock
  ApplicationLockClient applicationLockClient;
  @Mock
  MentoringRepository mentoringRepository;
  @InjectMocks
  private MentoringService mentoringService;

  @BeforeEach
  void initImpCode() {
    ReflectionTestUtils.setField(mentoringService, "impCode", "testCode");
  }

  @Test
  @DisplayName("예약 잠금 유효시간이 만료된상태에서 결제를 시도할 시, ReservationTimeExpiredException이 발생합니다.")
  void createPaymentInfoThrowExceptionTest() {
    //given
    Long id = 1L;
    MentoringApplicationUserInfo userInfo = MentoringApplicationUserInfo.builder()
        .nickname("testNickName")
        .build();
    String merchantUid = "test_uid";
    Mentor mentor = Mentor.builder()
        .build();
    ReflectionTestUtils.setField(mentor, "id", 1L);
    Mentoring mentoring = Mentoring.builder()
        .mentor(mentor)
        .build();
    given(mentoringRepository.findById(anyLong())).willReturn(Optional.of(mentoring));
    given(applicationLockClient.findByKey(anyString())).willThrow(ReservationTimeExpiredException.class);
    // when, then
    assertThatThrownBy(
        () -> mentoringService.createPaymentInfo(id, userInfo, merchantUid, "redisLockKey"))
        .isInstanceOf(ReservationTimeExpiredException.class);
  }

}
