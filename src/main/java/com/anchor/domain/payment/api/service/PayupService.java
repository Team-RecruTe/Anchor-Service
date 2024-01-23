package com.anchor.domain.payment.api.service;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.payment.domain.Payup;
import com.anchor.domain.payment.domain.repository.PayupRepository;
import com.anchor.global.util.PayupClient;
import com.anchor.global.util.type.DateTimeRange;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayupService {

  private final PayupRepository payupRepository;
  private final PayupClient payupClient;

  @Transactional
  public void processMonthlyPayup() {
    DateTimeRange dateTimeRange = createMonthRange();
    List<Payup> payupList = payupRepository.findAllByMonthRange(dateTimeRange);
    Map<Mentor, Integer> mentorTotalAmount = new HashMap<>();
    payupList.forEach(payup -> mentorTotalAmount.merge(payup.getMentor(), payup.getAmount(), Integer::sum));
    Set<Mentor> payupFailMentors = new HashSet<>();
    try {
      mentorTotalAmount.keySet()
          .parallelStream()
          .filter(key -> payupClient.validateAccountHolder(key, payupFailMentors))
          .forEach(key -> payupClient.requestPayup(key, mentorTotalAmount.get(key), payupFailMentors));
      payupRepository.updateStatus(dateTimeRange, payupFailMentors);
    } catch (Exception e) {
      log.warn(e.getMessage());
    }
  }

  private DateTimeRange createMonthRange() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime thisMonth = now.with(TemporalAdjusters.firstDayOfMonth())
        .truncatedTo(ChronoUnit.DAYS);
    LocalDateTime lastMonth = thisMonth.minusMonths(1L);
    return DateTimeRange.of(lastMonth, thisMonth);
  }
}
