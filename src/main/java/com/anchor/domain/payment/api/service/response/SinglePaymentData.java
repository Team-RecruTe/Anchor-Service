package com.anchor.domain.payment.api.service.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SinglePaymentData implements Serializable {

  private Integer code;
  private String message;
  private PaymentDataDetail response;

  @Builder
  private SinglePaymentData(Integer code, String message, PaymentDataDetail response) {
    this.code = code;
    this.message = message;
    this.response = response;
  }

  public boolean statusCheck() {
    if (code == null) {
      throw new RuntimeException("잘못된 요청입니다.");
    }
    return code.equals(0) && message == null;
  }

  @Getter
  @NoArgsConstructor
  public static class PaymentDataDetail {

    private int amount;
    @JsonProperty("apply_num")
    private String applyNum;

    @JsonProperty("bank_code")
    private String bankCode;

    @JsonProperty("bank_name")
    private String bankName;

    @JsonProperty("buyer_addr")
    private String buyerAddr;

    @JsonProperty("buyer_email")
    private Object buyerEmail;

    @JsonProperty("buyer_name")
    private String buyerName;

    @JsonProperty("buyer_postcode")
    private String buyerPostcode;

    @JsonProperty("buyer_tel")
    private String buyerTel;

    @JsonProperty("cancel_amount")
    private int cancelAmount;

    @JsonProperty("cancel_history")
    private List<String> cancelHistory;

    @JsonProperty("cancel_reason")
    private String cancelReason;

    @JsonProperty("cancel_receipt_urls")
    private List<String> cancelReceiptUrls;

    @JsonProperty("cancelled_at")
    private int cancelledAt;

    @JsonProperty("card_code")
    private String cardCode;

    @JsonProperty("card_name")
    private String cardName;

    @JsonProperty("card_number")
    private String cardNumber;

    @JsonProperty("card_quota")
    private int cardQuota;

    @JsonProperty("card_type")
    private String cardType;

    @JsonProperty("cash_receipt_issued")
    private boolean cashReceiptIssued;

    private String channel;

    private String currency;

    @JsonProperty("custom_data")
    private String customData;

    @JsonProperty("customer_uid")
    private String customerUid;

    @JsonProperty("customer_uid_usage")
    private String customerUidUsage;

    @JsonProperty("emb_pg_provider")
    private String embPgProvider;

    private boolean escrow;

    @JsonProperty("fail_reason")
    private String failReason;

    @JsonProperty("failed_at")
    private int failedAt;

    @JsonProperty("imp_uid")
    private String impUid;

    @JsonProperty("merchant_uid")
    private String merchantUid;

    private String name;

    @JsonProperty("paid_at")
    private int paidAt;

    @JsonProperty("pay_method")
    private String payMethod;

    @JsonProperty("pg_id")
    private String pgId;

    @JsonProperty("pg_provider")
    private String pgProvider;

    @JsonProperty("pg_tid")
    private String pgTid;

    @JsonProperty("receipt_url")
    private String receiptUrl;

    @JsonProperty("started_at")
    private int startedAt;

    private String status;

    @JsonProperty("user_agent")
    private String userAgent;

    @JsonProperty("vbank_code")
    private String vBankCode;

    @JsonProperty("vbank_date")
    private int vBankDate;

    @JsonProperty("vbank_holder")
    private String vBankHolder;

    @JsonProperty("vbank_issued_at")
    private int vBankIssuedAt;

    @JsonProperty("vbank_name")
    private String vBankName;

    @JsonProperty("vbank_num")
    private String vBankNum;

    @Builder
    private PaymentDataDetail(int amount, String impUid, String merchantUid) {
      this.amount = amount;
      this.impUid = impUid;
      this.merchantUid = merchantUid;
    }
  }
}
