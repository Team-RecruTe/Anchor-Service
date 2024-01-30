package com.anchor.global.exception.type.api;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.ServiceErrorCode;
import com.anchor.global.exception.response.SimpleErrorDetail;

/**
 * 외부 API 호출시에 발생하는 예외입니다.
 */
public class ApiClientException extends ServiceException {

  /**
   * 상태코드 500일때 사용합니다.
   */
  public ApiClientException() {
    super(ServiceErrorCode.EXTERNAL_API_ERROR);
  }

  /**
   * 송금API 사용시 응답 바디의 데이터 중 ResponseCode가 '00000'이 아닐때 사용합니다.
   * <br>
   * 결제API 사용시 응답 상태코드 400번대 중 401, 404 이외일 시 사용합니다.
   * <br>
   * example :: 유효하지않은 파라미터로 요청 또는 필수파라미터 누락
   *
   * @param message 응답 바디의 Message
   */
  public ApiClientException(String message) {
    super(ServiceErrorCode.INVALID_PARAM, new SimpleErrorDetail(message));
  }

}
