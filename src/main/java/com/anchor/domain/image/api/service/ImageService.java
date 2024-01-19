package com.anchor.domain.image.api.service;

import com.anchor.domain.image.api.controller.request.ImageFile;
import com.anchor.domain.image.api.service.response.S3ImageInfo;
import com.anchor.domain.image.domain.Image;
import com.anchor.domain.image.domain.repository.ImageRepository;
import com.anchor.global.aws.AwsS3Utils;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImageService {

  private final ImageRepository imageRepository;
  private final AwsS3Utils awsS3Utils;

  @Transactional
  public S3ImageInfo save(ImageFile file) {
    String url = awsS3Utils.uploadFile(file.getImage());
    Image savedImage = imageRepository.save(Image.of(url));
    Long imageId = savedImage.getId();
    return S3ImageInfo.of(imageId, url);
  }

  @Scheduled(cron = "${cloud.aws.cron}")
  @Transactional
  public void deleteUnusedImage() {
    List<Image> unusedImages = imageRepository.findUnusedImages();
    List<String> urls = unusedImages.stream()
        .map(Image::getUrl)
        .toList();
    awsS3Utils.deleteFiles(urls);
    imageRepository.deleteAll(unusedImages);
  }

}
