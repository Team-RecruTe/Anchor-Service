package com.anchor.domain.mentor.domain;

import com.anchor.global.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MentorIntroduction extends BaseEntity {

  @Column(nullable = false, columnDefinition = "mediumtext")
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