package com.anchor.domain.mentor.domain;

import com.anchor.domain.mentor.api.controller.request.MentorIntroductionRequest;
import com.anchor.global.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MentorIntroduction extends BaseEntity {

  @Lob
  @Column(nullable = false)
  private String contents;

  private MentorIntroduction(String contents){
    this.contents = contents;
  }

  public static MentorIntroduction addContents(String contents){
    return new MentorIntroduction(contents);
  }

  public void editContents(String contents){
    this.contents = contents;
  }

}
