package com.anchor.domain.image.api.controller.request;

import com.anchor.global.util.valid.ValidFile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
public class ImageFile {

  @ValidFile(message = "이미지 파일은 필수입니다.")
  private MultipartFile image;

}
