package com.anchor.global.aws;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.InputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Component
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

  public void deleteFiles(List<String> urls) {
    try {
      DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
      List<KeyVersion> fileKeys = urls.stream()
          .map(url -> {
            String[] split = url.split("/");
            String key = split[split.length - 1];
            return new KeyVersion(key);
          })
          .toList();
      deleteObjectsRequest.setKeys(fileKeys);
      amazonS3Client.deleteObjects(deleteObjectsRequest);
    } catch (SdkClientException e) {
      throw new RuntimeException("Error deleting file from S3", e);
    }
  }

  public void deleteFile() {
    amazonS3Client.deleteObject(new DeleteObjectRequest(bucketName, "스크린샷 2023-12-20 오후 3.46.38.png"));
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