package com.anchor.domain.image.api.controller;

import com.anchor.domain.image.api.controller.request.ImageFile;
import com.anchor.domain.image.api.service.ImageService;
import com.anchor.domain.image.api.service.response.S3ImageInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/image/upload")
@RestController
public class ImageController {

  private final ImageService imageService;

  @PostMapping
  public ResponseEntity<S3ImageInfo> uploadImage(@Valid @ModelAttribute ImageFile imageFile) {
    S3ImageInfo s3ImageInfo = imageService.save(imageFile);
    return ResponseEntity.ok(s3ImageInfo);
  }

}
