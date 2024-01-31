package com.anchor.domain.user.api.service.response;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.user.domain.User;
import com.anchor.domain.user.domain.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserInfoResponse {

  private String email;
  private String nickname;
  private String image;
  private UserRole role;
  private Mentor mentor;
  private List<MentoringApplication> mentoringApplicationList = new ArrayList<>();

  public UserInfoResponse(User user){
    this.email = user.getEmail();
    this.nickname = user.getNickname();
    this.image = user.getImage();
    this.role = user.getRole();
    this.mentor = user.getMentor();
    this.mentoringApplicationList = user.getMentoringApplicationList();
  }

}
