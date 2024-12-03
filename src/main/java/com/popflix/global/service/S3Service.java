package com.popflix.global.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.popflix.global.exception.FileDeleteException;
import com.popflix.global.exception.FileUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadImage(MultipartFile file) {
        String fileName = generateUniqueFileName(file.getOriginalFilename());
        return uploadWithMetadata(file, fileName);
    }

    public String uploadProfileImage(MultipartFile file, Long userId) {
        try {
            String fileName = String.format("profiles/%d/%s", userId, generateUniqueFileName(file.getOriginalFilename()));
            return uploadWithMetadata(file, fileName);
        } catch (Exception e) {
            throw new FileUploadException("Failed to upload profile image", e);
        }
    }

    public String uploadDefaultImage(File file, String fileName) {
        try {
            String key = "defaults/" + fileName;
            amazonS3.putObject(new PutObjectRequest(bucket, key, file));
            return amazonS3.getUrl(bucket, key).toString();
        } catch (Exception e) {
            throw new FileUploadException("Failed to upload default image", e);
        }
    }

    public void deleteImage(String imageUrl) {
        try {
            String fileName = extractFileNameFromUrl(imageUrl);
            amazonS3.deleteObject(bucket, fileName);
            log.info("Successfully deleted image: {}", fileName);
        } catch (Exception e) {
            throw new FileDeleteException("Failed to delete file from S3", e);
        }
    }

    private String uploadWithMetadata(MultipartFile file, String fileName) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            amazonS3.putObject(new PutObjectRequest(
                    bucket,
                    fileName,
                    file.getInputStream(),
                    metadata
            ));

            String fileUrl = amazonS3.getUrl(bucket, fileName).toString();
            log.info("Successfully uploaded file: {}", fileName);
            return fileUrl;

        } catch (IOException e) {
            throw new FileUploadException("Failed to upload file to S3", e);
        }
    }

    private String generateUniqueFileName(String originalFileName) {
        return UUID.randomUUID().toString() + "_" + originalFileName;
    }

    private String extractFileNameFromUrl(String imageUrl) {
        String bucketPrefix = bucket + ".s3.amazonaws.com/";
        if (imageUrl.contains(bucketPrefix)) {
            return imageUrl.substring(imageUrl.indexOf(bucketPrefix) + bucketPrefix.length());
        }
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    }
}