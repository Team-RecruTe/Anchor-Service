package com.anchor.domain.mentoring.domain.repository;

import com.anchor.domain.mentoring.api.controller.request.MentoringRatingInterface;
import com.anchor.domain.mentoring.api.controller.request.MentoringReviewInfoInterface;
import com.anchor.domain.mentoring.domain.MentoringReview;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MentoringReviewRepository extends JpaRepository<MentoringReview, Long> {

  //쿼리문 작성하기
  @Query(
      value =
          "select mr.contents, mr.ratings, u.nickname "
              + "from mentoring_review as mr "
              + "join mentoring_application as ma "
              + "on mr.mentor_application_id = ma.id "
              + "join mentoring as m "
              + "on ma.mentoring_id = m.id "
              + "join user as u "
              + "on ma.user_id = u.id "
              + "where ma.mentoring_id = :mentoringId ", nativeQuery = true)
  List<MentoringReviewInfoInterface> getReviewList(@Param("mentoringId") Long mentoringId);

  @Query(
      value = "select round(avg(mr.ratings), 2) as average_ratings\n"
          + "from mentoring_review as mr\n"
          + "join mentoring_application as ma\n"
          + "on mr.mentor_application_id = ma.id\n"
          + "join mentoring as m\n"
          + "on ma.mentoring_id = m.id \n"
          + "join user as u\n"
          + "on ma.user_id = u.id\n"
          + "where ma.mentoring_id = :mentoringId\n"
          + "group by ma.mentoring_id", nativeQuery = true
  )
  MentoringRatingInterface getAverageRatings(@Param("mentoringId") Long mentoringId);
}