package com.anchor.global.redis.client;

import com.anchor.global.auth.SessionUser;
import com.anchor.global.util.type.DateTimeRange;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationTimeInfo {

  private String email;

  private DateTimeRange reservedTime;

  private ReservationTimeInfo(String email, DateTimeRange reservedTime) {
    this.email = email;
    this.reservedTime = reservedTime;
  }

  public static ReservationTimeInfo of(SessionUser sessionUser, DateTimeRange reservedTime) {
    return new ReservationTimeInfo(sessionUser.getEmail(), reservedTime);
  }

  public boolean isOwner(String email) {
    return this.email.equals(email);
  }

}
