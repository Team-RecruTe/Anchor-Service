package com.anchor.global.auth;

import com.anchor.domain.user.domain.User;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SessionUser implements Serializable {

  private Long id;
  private String email;
  private String nickname;
  private String image;
  private Long mentorId;

  public SessionUser(User user) {
    this.id = user.getId();
    this.email = user.getEmail();
    this.nickname = user.getNickname();
    this.image = user.getImage();
    if (Objects.nonNull(user.getMentor())) {
      this.mentorId = user.getMentor()
          .getId();
    }
  }

  public static SessionUser getSessionUser(HttpSession session) {
    SessionUser sessionUser = (SessionUser) session.getAttribute("user");
    if (Objects.nonNull(sessionUser)) {
      return sessionUser;
    }
    throw new RuntimeException("로그인 정보가 존재하지 않습니다.");
  }

  public void addMentorId(Long mentorId) {
    this.mentorId = mentorId;
  }
}
