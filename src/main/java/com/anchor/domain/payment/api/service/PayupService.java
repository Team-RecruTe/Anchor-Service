package com.anchor.domain.payment.api.service;

import com.anchor.domain.mentoring.domain.repository.MentoringApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class PayupService {

    private final MentoringApplicationRepository mentoringApplicationRepository;


    public void payupProcess() {
        //현재시간
        LocalDateTime now = LocalDateTime.now();

        //이번달 1일 00시 00분 00초
        LocalDateTime startOfNowMonth = LocalDateTime.of(now.toLocalDate().withDayOfMonth(1), LocalTime.MIN);
        //지난달 1일 00시 00분 00초
        LocalDateTime startOfLastMonth = startOfNowMonth.minusMonths(1L);

        //지난달 1일 이후 ~ 이번달 1일 미만에 업데이트 일자를 가지고, 상태가 Complete 인 MentoringApplication 객체 조회


    }
}
