package com.anchor.domain.image.domain.repository;

import com.anchor.domain.image.domain.Image;
import com.anchor.domain.image.domain.repository.custom.QImageRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long>, QImageRepository {

}
