package com.anchor.domain.image.api.controller.request;

import com.anchor.global.util.valid.ValidFile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class ImageFile {

  @ValidFile(message = "이미지 파일은 필수입니다.")
  private MultipartFile image;

}
