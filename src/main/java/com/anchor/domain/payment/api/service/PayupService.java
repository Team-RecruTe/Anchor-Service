package com.anchor.domain.payment.api.service;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.domain.mentoring.domain.repository.MentoringApplicationRepository;
import com.anchor.domain.payment.domain.Payment;
import com.anchor.domain.payment.domain.repository.PayupRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PayupService {

  private final MentoringApplicationRepository mentoringApplicationRepository;
  private final PayupRepository payupRepository;

  /**
   * 매달 1일에 MentoringStatus 가 COMPLETE 인 데이터에 한해 정산을 진행합니다. updateDate 컬럼의 데이터가 지난달 1일 00:00:00 ~ 말일 23:59:59 인 데이터에 한해
   * 작업을 진행합니다.<p> 멘토의 등록 은행, 계좌번호를 사용해 정산을 진행할 예정입니다.<p> 현재 계좌송금 api를 찾지못해 미구현입니다.
   */
  public void payupProcess(MentoringStatus status) {
    LocalDateTime now = LocalDateTime.now();

    LocalDateTime thisMonth = LocalDateTime.of(now.toLocalDate()
        .withDayOfMonth(1), LocalTime.MIN);
    //지난달 1일 00시 00분 00초
    LocalDateTime lastMonth = thisMonth.minusMonths(1L);

    //매칭되는 시간 조회
    List<MentoringApplication> lastMonthCompleteMentoringList = mentoringApplicationRepository.findPayupListByCompleteAndLastMonth(
        status, lastMonth, thisMonth);

    for (MentoringApplication mentoringApplication : lastMonthCompleteMentoringList) {
      Mentoring mentoring = mentoringApplication.getMentoring();
      Mentor mentor = mentoring
          .getMentor();

      Payment payment = mentoringApplication.getPayment();

      // 송금 필요 데이터
      String bankName = mentor.getBankName();
      String accountNumber = mentor.getAccountNumber();
      Integer amount = payment.getAmount();

      // 정산처리(송금)이 완료되었다고 가정
//      Payup payup = payment.getPayup();
//      payup.changeStatusToComplete();
//      payupRepository.save(payup);

    }

  }


}
