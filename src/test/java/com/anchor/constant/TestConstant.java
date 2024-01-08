package com.anchor.constant;

import java.time.format.DateTimeFormatter;

public class TestConstant {

  public static final String NICKNAME = "테스트유저";
  public static final String USER_EMAIL = "testUser@test.com";
  public static final String MENTORING_TITLE = "테스트타이틀";
  public static final String MENTORING_CONTENT = "테스트내용";
  public static final String DURATION_TIME = "1시간";
  public static final Integer COST = 10_000;
  public static final String FIRST_FROM_DATE_TIME = "2024/01/01 00:00:00";
  public static final String FIRST_TO_DATE_TIME = "2024/01/01 23:59:59";
  public static final String SECOND_FROM_DATE_TIME = "2024/01/02 13:00:00";
  public static final String SECOND_TO_DATE_TIME = "2024/01/02 14:00:00";
  public static final String APPLICATION_DATE = "2024/01/03";
  public static final String APPLICATION_TIME = "13:00";
  public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
  public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
}

