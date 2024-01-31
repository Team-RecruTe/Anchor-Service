package com.anchor.domain.image.domain.repository.custom;

import static com.anchor.domain.image.domain.QImage.image;

import com.anchor.domain.image.domain.Image;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class QImageRepositoryImpl implements QImageRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public List<Image> findUnusedImages() {
    return jpaQueryFactory.selectFrom(image)
        .where(
            image.mentoringDetailId.isNull()
                .and(image.mentorIntroductionId.isNull())
        )
        .fetch();
  }

}
