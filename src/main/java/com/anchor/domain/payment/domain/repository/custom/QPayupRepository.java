package com.anchor.domain.payment.domain.repository.custom;

import com.anchor.domain.mentor.api.service.response.MentorPayupResult.PayupInfo;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.payment.domain.Payup;
import com.anchor.global.util.type.DateTimeRange;
import java.util.List;
import java.util.Set;

public interface QPayupRepository {

  List<PayupInfo> findAllByMonthRange(DateTimeRange dateTimeRange, Long mentorId);

  List<Payup> findAllByMonthRange(DateTimeRange dateTimeRange);

  void updateStatus(DateTimeRange dateTimeRange, Set<Mentor> failMentors);
}
