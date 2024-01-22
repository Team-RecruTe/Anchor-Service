package com.anchor.domain.payment.domain.repository.custom;

import com.anchor.domain.mentor.api.service.response.MentorPayupResult.PayupInfo;
import com.anchor.global.util.type.DateTimeRange;
import java.util.List;

public interface QPayupRepository {

  List<PayupInfo> findAllByMonthRange(DateTimeRange dateTimeRange, Long mentorId);
}
