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
import java.util.concurrent.ForkJoinPool;
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
    Map<Mentor, Integer> mentorTotalAmount = mergePayupAmount(payupList);
    Set<Mentor> payupFailMentors = processPayup(mentorTotalAmount);
    payupRepository.updateStatus(dateTimeRange, payupFailMentors);
  }

  private DateTimeRange createMonthRange() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime thisMonth = now.with(TemporalAdjusters.firstDayOfMonth())
        .truncatedTo(ChronoUnit.DAYS);
    LocalDateTime lastMonth = thisMonth.minusMonths(1L);
    return DateTimeRange.of(lastMonth, thisMonth);
  }

  private Map<Mentor, Integer> mergePayupAmount(List<Payup> payupList) {
    Map<Mentor, Integer> mentorTotalAmount = new HashMap<>();
    payupList.forEach(payup -> mentorTotalAmount.merge(payup.getMentor(), payup.getAmount(), Integer::sum));
    return mentorTotalAmount;
  }

  private Set<Mentor> processPayup(Map<Mentor, Integer> mentorTotalAmount) {
    Set<Mentor> payupFailMentors = new HashSet<>();
    ForkJoinPool customForkJoinPool = new ForkJoinPool(Runtime.getRuntime()
        .availableProcessors());
    mentorTotalAmount.keySet()
        .parallelStream()
        .filter(key -> payupClient.validateAccountHolder(key, payupFailMentors))
        .forEach(key -> payupClient.requestPayup(key, mentorTotalAmount.get(key), payupFailMentors));
    customForkJoinPool.shutdown();
    return payupFailMentors;
  }

}