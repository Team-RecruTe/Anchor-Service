package com.anchor.domain.user.api.controller.request;

import com.anchor.domain.mentoring.domain.MentoringReview;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.beans.ConstructorProperties;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RequiredEditReview {

  private Long id;

  @NotBlank(message = "상세 내용을 입력해주세요.")
  @Size(min = 1, max = 200, message = "1자 이상 200자 이하로 작성해주세요.")
  private String contents;

  private Integer ratings;

  @Builder
  @ConstructorProperties({"id", "contents", "ratings"})
  private RequiredEditReview(Long id, String contents, Integer ratings) {
    this.id = id;
    this.contents = contents;
    this.ratings = ratings;
  }

  public static RequiredEditReview of(MentoringReview review) {
    return new RequiredEditReview(review.getId(), review.getContents(), review.getRatings());
  }
}
