package com.anchor.domain.mentor.api.controller.request;

import java.util.Random;

public class RandomCodeMaker {
  public static String makeRandomCode() {
    StringBuilder sb = new StringBuilder();
    Random rand = new Random();

    for (int i = 0; i < 8; i++) {
      sb.append((char) (rand.nextInt(26) + 65));
    }
    for (int i = 0; i < 4; i++) {
      sb.append((int) (Math.random() * 10));
    }
    return sb.toString();
  }
}
