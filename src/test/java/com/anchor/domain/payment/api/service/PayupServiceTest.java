package com.anchor.domain.payment.api.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.payment.domain.Payment;
import com.anchor.domain.payment.domain.Payup;
import com.anchor.domain.payment.domain.repository.PayupRepository;
import com.anchor.global.util.type.DateTimeRange;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
class PayupServiceTest {

  @MockBean
  PayupRepository payupRepository;

  @Autowired
  PayupService payupService;

  @Test
  @DisplayName("정산작업 병렬처리 성능테스트")
  void parallelPayupTest() {
    //given
    List<Mentor> mentorList = createMentorList();
    List<Payup> payupList = createPayupList(mentorList);
    when(payupRepository.findAllByMonthRange(any(DateTimeRange.class))).thenReturn(payupList);

    //when
    long startTime = System.currentTimeMillis();
    payupService.processMonthlyPayup();
    long endTime = System.currentTimeMillis();
    long workTime = endTime - startTime;
    log.info("workTime :: {} ms", workTime);
    //then
    verify(payupRepository, times(1)).updateStatus(any(DateTimeRange.class), any());
  }

  List<Payup> createPayupList(List<Mentor> mentorList) {
    List<Payup> payups = new ArrayList<>();
    for (int i = 1; i <= 10000; i++) {
      int mentorIndex = i % 1000;
      Mentor mentor = mentorList.get(mentorIndex);
      Payment payment = Payment.builder()
          .amount(10000)
          .merchantUid("anchor_12344567")
          .build();
      Payup payup = Payup.builder()
          .mentor(mentor)
          .payment(payment)
          .build();
      payups.add(payup);
    }
    return payups;
  }

  List<Mentor> createMentorList() {
    List<Mentor> mentors = new ArrayList<>();
    Mentor mentor1 = Mentor.builder()
        .companyEmail("test1")
        .bankName("농협은행")
        .accountName("기홍")
        .accountNumber("3020000010019")
        .build();

    Mentor mentor2 = Mentor.builder()
        .companyEmail("test2")
        .bankName("산업은행")
        .accountName("기홍")
        .accountNumber("1000002661002")
        .build();

    Mentor mentor3 = Mentor.builder()
        .companyEmail("test3")
        .bankName("산업은행")
        .accountName("기홍")
        .accountNumber("1000002661002")
        .build();

    Mentor mentor4 = Mentor.builder()
        .companyEmail("test4")
        .bankName("산업은행")
        .accountName("기홍")
        .accountNumber("1000002661002")
        .build();

    Mentor mentor5 = Mentor.builder()
        .companyEmail("test5")
        .bankName("산업은행")
        .accountName("기홍")
        .accountNumber("1000002661002")
        .build();

    Mentor mentor6 = Mentor.builder()
        .companyEmail("test6")
        .bankName("산업은행")
        .accountName("기홍")
        .accountNumber("1000002661002")
        .build();

    Mentor mentor7 = Mentor.builder()
        .companyEmail("test7")
        .bankName("산업은행")
        .accountName("기홍")
        .accountNumber("1000002661002")
        .build();

    Mentor mentor8 = Mentor.builder()
        .companyEmail("test8")
        .bankName("산업은행")
        .accountName("기홍")
        .accountNumber("1000002661002")
        .build();

    Mentor mentor9 = Mentor.builder()
        .companyEmail("test9")
        .bankName("산업은행")
        .accountName("기홍")
        .accountNumber("1000002661002")
        .build();

    Mentor mentor10 = Mentor.builder()
        .companyEmail("test10")
        .bankName("산업은행")
        .accountName("기홍")
        .accountNumber("1000002661002")
        .build();
    mentors.add(mentor1);
    mentors.add(mentor2);
    mentors.add(mentor3);
    mentors.add(mentor4);
    mentors.add(mentor5);
    mentors.add(mentor6);
    mentors.add(mentor7);
    mentors.add(mentor8);
    mentors.add(mentor9);
    mentors.add(mentor10);

    for (int i = 1; i <= 990; i++) {
      Mentor build = Mentor.builder()
          .companyEmail("test" + (10 + i))
          .bankName("산업은행")
          .accountName("기홍")
          .accountNumber("1000002661002")
          .build();
      mentors.add(build);
    }
    return mentors;
  }
}