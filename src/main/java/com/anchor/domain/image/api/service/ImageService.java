package com.anchor.domain.image.api.service;

import com.anchor.domain.image.api.controller.request.ImageFile;
import com.anchor.domain.image.api.service.response.S3ImageUrl;
import com.anchor.domain.image.domain.Image;
import com.anchor.domain.image.domain.repository.ImageRepository;
import com.anchor.global.aws.AwsS3Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImageService {

  private final ImageRepository imageRepository;
  private final AwsS3Utils awsS3Utils;

  public S3ImageUrl save(ImageFile file) {
    String url = awsS3Utils.uploadFile(file.getImage());
    imageRepository.save(Image.of(url));
    return S3ImageUrl.of(url);
  }

}
