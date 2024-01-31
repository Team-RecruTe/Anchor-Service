package com.anchor.domain.image.domain;

import com.anchor.domain.mentor.domain.MentorIntroduction;
import com.anchor.domain.mentoring.domain.MentoringDetail;
import com.anchor.global.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Image extends BaseEntity {

  @Column
  private String url;

  @Column
  private Long mentoringDetailId;

  @Column
  private Long mentorIntroductionId;

  private Image(String url) {
    this.url = url;
  }

  public static Image of(String url) {
    return new Image(url);
  }

  public static void mapMentoringDetails(MentoringDetail mentoringDetail, List<Image> savedImages) {
    savedImages.forEach(image -> {
      image.mentoringDetailId = mentoringDetail.getId();
    });
  }

  public static void mapMentorIntroduction(MentorIntroduction mentorIntroduction, List<Image> savedImages) {
    savedImages.forEach(image -> {
      image.mentorIntroductionId = mentorIntroduction.getId();
    });
  }

}
