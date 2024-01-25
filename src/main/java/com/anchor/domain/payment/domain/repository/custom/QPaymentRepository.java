package com.anchor.domain.payment.domain.repository.custom;

import com.anchor.domain.payment.api.service.response.PaymentCompleteResult;
import com.anchor.domain.payment.domain.Payment;
import java.util.List;

public interface QPaymentRepository {

  List<Payment> findPaymentListStartWithToday(String today);

  PaymentCompleteResult findCompletedPaymentByOrderUid(String orderUid);
}
