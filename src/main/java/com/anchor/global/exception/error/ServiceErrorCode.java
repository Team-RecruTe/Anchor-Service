package com.anchor.global.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ServiceErrorCode {

  CUSTOM_ERROR_CODE(HttpStatus.BAD_REQUEST, "커스텀 에러 코드"),

  ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "데이터가 존재하지 않습니다."),

  ENTITY_NOT_UNIQUE(HttpStatus.CONFLICT, "복수의 데이터가 존재합니다."),

  DUPLICATE_COMPANY_EMAIL(HttpStatus.CONFLICT, "이미 등록된 회사 메일입니다."),

  FUTURE_DATE(HttpStatus.BAD_REQUEST, "미래시점의 정산내역 조회는 불가능합니다."),

  INVALID_STATUS(HttpStatus.CONFLICT, "변경 불가능한 상태를 요청하였습니다."),

  ATTRIBUTE_NOT_FOUND(HttpStatus.NOT_FOUND, "주어진 속성 필드가 존재하지 않습니다."),

  NOT_LOGIN(HttpStatus.NOT_FOUND, "로그인 정보가 존재하지 않습니다."),

  MAIL_FAIL(HttpStatus.SERVICE_UNAVAILABLE, "메일 발송에 실패하였습니다."),

  SERIALRIZATION_FAIL(HttpStatus.CONFLICT, "Null 또는 직렬화 불가능한 객체입니다."),

  DESERIALIZATION_FAIL(HttpStatus.CONFLICT, "역직렬화에 실패하였습니다."),

  HTTP_ERROR(HttpStatus.BAD_GATEWAY, "HTTP 통신 중 에러가 발생하였습니다."),

  INVALID_PAYMENT(HttpStatus.CONFLICT, "결제 검증에 실패하였습니다."),

  INVALID_IMP_UID(HttpStatus.NOT_FOUND, "유효하지않은 imp_uid입니다."),

  INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "액세스토큰이 유효하지않거나 전달되지 않았습니다."),

  INVALID_PARAM(HttpStatus.BAD_REQUEST, "파라미터가 유효하지 않습니다."),

  EXTERNAL_API_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "외부 API서버 오류입니다."),

  BANK_NAME_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 은행명이 존재하지 않습니다."),

  EXPIRED_TIME(HttpStatus.UNAUTHORIZED, "이미 만료된 Lock입니다."),

  LOCK_ACQUIRE_FAIL(HttpStatus.BAD_GATEWAY, "Lock 획득에 실패하였습니다."),

  IMAGE_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 삭제에 실패하였습니다."),

  IMAGE_UPLOAD_ERROR(HttpStatus.UNPROCESSABLE_ENTITY, "이미지 업로드에 실패하였습니다.");

  private final HttpStatus status;
  private final String message;

}
