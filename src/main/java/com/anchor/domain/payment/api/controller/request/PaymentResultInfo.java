package com.anchor.domain.payment.api.controller.request;

import com.anchor.global.portone.response.SinglePaymentResult;
import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentResultInfo implements Serializable {

  private String impUid;
  private String merchantUid;
  private Integer amount;

  @Builder
  private PaymentResultInfo(String impUid, String merchantUid, Integer amount) {
    this.impUid = impUid;
    this.merchantUid = merchantUid;
    this.amount = amount;
  }

  public boolean isSameAs(SinglePaymentResult result) {
    return impUid.equals(result.getImpUid())
        && merchantUid.equals(result.getMerchantUid())
        && amount.equals(result.getAmount());
  }

}
