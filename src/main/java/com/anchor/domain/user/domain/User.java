package com.anchor.domain.user.domain;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.user.api.controller.request.UserImageRequest;
import com.anchor.domain.user.api.controller.request.UserNicknameRequest;
import com.anchor.global.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends BaseEntity {

  @Column(length = 50, nullable = false)
  private String email;

  @Column(length = 50, nullable = false)
  private String nickname;

  private String image;

  @Enumerated(EnumType.STRING)
  private UserRole role;

  @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
  private Mentor mentor;

  @OneToMany(mappedBy = "user")
  private List<MentoringApplication> mentoringApplicationList = new ArrayList<>();

  @Builder
  public User(String email, String nickname, String image, UserRole role) {
    this.email = email;
    this.nickname = nickname;
    this.image = image;
    this.role = role;
  }

  public String getRoleKey() {
    return role.getKey();
  }

  public void editNickname(UserNicknameRequest userNicknameRequest){
    this.nickname = userNicknameRequest.getNickname();
  }

  public void uploadImage(UserImageRequest userImageRequest){
    this.image = userImageRequest.getImage();
  }

}