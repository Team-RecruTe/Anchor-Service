package com.anchor.domain.image.domain;

import com.anchor.domain.mentor.domain.MentorIntroduction;
import com.anchor.domain.mentoring.domain.MentoringDetail;
import com.anchor.global.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name="image")
public class Image extends BaseEntity {

  private String url;

  @Column(name="mentoring_detail_id")
  private Long mentoringDetailId;

  @Column(name="mentor_introduction_id")
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
