package com.popflix.global.service;

import com.amazonaws.services.s3.AmazonS3;
import com.popflix.global.exception.FileDeleteException;
import com.popflix.global.exception.FileUploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadImage(MultipartFile file) {
        try {
            String fileName = generateUniqueFileName(file.getOriginalFilename());
            amazonS3.putObject(bucket, fileName, file.getInputStream(), null);
            return amazonS3.getUrl(bucket, fileName).toString();
        } catch (IOException e) {
            throw new FileUploadException("Failed to upload file to S3", e);
        }
    }

    public void deleteImage(String imageUrl) {
        try {
            String fileName = extractFileNameFromUrl(imageUrl);
            amazonS3.deleteObject(bucket, fileName);
        } catch (Exception e) {
            throw new FileDeleteException("Failed to delete file from S3", e);
        }
    }

    private String generateUniqueFileName(String originalFileName) {
        return UUID.randomUUID().toString() + "_" + originalFileName;
    }

    private String extractFileNameFromUrl(String imageUrl) {
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    }
}
