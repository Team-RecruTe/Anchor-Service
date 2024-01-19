package com.anchor.domain.image.domain.repository.custom;

import com.anchor.domain.image.domain.Image;
import java.util.List;

public interface QImageRepository {

  List<Image> findUnusedImages();
}
