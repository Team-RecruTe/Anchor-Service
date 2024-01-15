package com.anchor.global.portone.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RequiredPaymentCreateData implements RequiredPaymentData {

  private String impUid;

  public RequiredPaymentCreateData(String impUid) {
    this.impUid = impUid;
  }

}
