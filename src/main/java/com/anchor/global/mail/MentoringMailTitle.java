package com.anchor.global.mail;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MentoringMailTitle {

  APPLY_BY_MENTEE("새롭게 접수된 멘토링 신청내역이 있습니다! 자세한 내용을 확인해주세요."),
  CANCEL_BY_MENTEE("멘티님께서 멘토링을 취소하였습니다. 자세한 내용을 확인해주세요."),
  COMPLETE_BY_MENTEE("멘티님께서 멘토링을 완료하였습니다. 자세한 내용을 확인해주세요."),
  CANCEL_BY_MENTOR("멘토님께서 멘토링을 취소하였습니다. 자세한 내용을 확인해주세요."),
  APPROVE_BY_MENTOR("멘티님께서 멘토링을 수락하셨습니다. 자세한 내용을 확인해주세요.");

  private final String title;

}
