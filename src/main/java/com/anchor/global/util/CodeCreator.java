package com.anchor.global.util;

import static lombok.AccessLevel.PRIVATE;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jodd.util.RandomString;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class CodeCreator {

  public static String createMerchantUid(String userEmail) {
    String shuffleEmail = shuffleEmail(userEmail);
    String today = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    return "toss_" + shuffleEmail + today;
  }

  public static String createEmailAuthCode() {
    RandomString randomString = new RandomString();
    return randomString.randomAlphaNumeric(12);
  }

  private static String shuffleEmail(String email) {
    StringBuilder sb = new StringBuilder();
    List<Character> characters = new ArrayList<>();
    String parseEmail = email.substring(0, email.indexOf('@'));
    for (char token : parseEmail.toCharArray()) {
      characters.add(token);
    }
    Collections.shuffle(characters);
    characters.forEach(sb::append);
    return sb.toString();
  }

}
