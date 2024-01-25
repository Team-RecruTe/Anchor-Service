package com.anchor.domain.mentoring.api.service.response;

import com.anchor.domain.mentoring.domain.MentoringReview;
import com.anchor.domain.user.domain.User;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class MentoringReviewSearchResult {

  private List<ReviewInfo> reviewInfos;
  private String averageRatings;

  private MentoringReviewSearchResult(List<ReviewInfo> reviewInfos, String averageRatings) {
    this.reviewInfos = reviewInfos;
    this.averageRatings = averageRatings;
  }

  public static MentoringReviewSearchResult of(List<MentoringReview> mentoringReviews) {
    List<ReviewInfo> reviewInfos = mentoringReviews.stream()
        .map(ReviewInfo::of)
        .toList();
    double averageRatings = mentoringReviews.stream()
        .mapToInt(MentoringReview::getRatings)
        .average()
        .orElse(0);
    DecimalFormat decimalFormat = new DecimalFormat("0.#");
    decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
    return new MentoringReviewSearchResult(reviewInfos, decimalFormat.format(averageRatings));
  }

  @Getter
  @AllArgsConstructor
  static class ReviewInfo {

    private String userNickname;
    private String contents;
    private Integer ratings;

    static ReviewInfo of(MentoringReview review) {
      User user = review.getMentoringApplication()
          .getUser();
      return new ReviewInfo(user.getNickname(), review.getContents(), review.getRatings());
    }
  }
}
