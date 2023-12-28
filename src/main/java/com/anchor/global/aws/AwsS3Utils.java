package com.anchor.global.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
public class AwsS3Utils {

  private final AmazonS3 amazonS3Client;

  @Value("${cloud.aws.s3.bucket}")
  private String bucketName;

  public String uploadFile(MultipartFile file) {
    String fileName = ImageFileResolver.buildFileName(file.getOriginalFilename());

    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentType(file.getContentType());
    try (InputStream inputStream = file.getInputStream()) {
      amazonS3Client.putObject(
          new PutObjectRequest(bucketName, fileName, inputStream, objectMetadata)
              .withCannedAcl(CannedAccessControlList.PublicRead));
    } catch (Exception e) {
      log.error("Exeption is {}", e);
      throw new RuntimeException("이미지를 업로드할 수 없습니다.");
    }

    return amazonS3Client.getUrl(bucketName, fileName)
        .toString();
  }

  private static class ImageFileResolver {

    private static final String FILE_EXTENSION_SEPARATOR = ".";

    public static String buildFileName(String originalFileName) {
      int fileExtensionIndex = originalFileName.lastIndexOf(FILE_EXTENSION_SEPARATOR); //파일 확장자 구분선
      String fileExtension = originalFileName.substring(fileExtensionIndex); //파일 확장자
      String fileName = originalFileName.substring(0, fileExtensionIndex); // 파일 이름
      String now = String.valueOf(System.currentTimeMillis()); // 파일 업로드 시간

      return fileName + "_" + now + fileExtension;
    }

  }


}

