package com.zekiloni.george.attachment.infrastructure.storage;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

@Component
@ConditionalOnProperty(name = "app.storage.type", havingValue = "s3")
@RequiredArgsConstructor
public class S3FileStorageService implements FileStorageService {
    private final AmazonS3Client amazonS3Client;
    private final String bucketName = "vectorhaul-attachments";

    @Override
    public String storeFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        amazonS3Client.putObject(
                new PutObjectRequest(
                        bucketName,
                        fileName,
                        file.getInputStream(),
                        metadata
                )
        );

        return amazonS3Client.getUrl(bucketName, fileName).toString();
    }

    @Override
    public Path getFilePath(String href) {
        throw new UnsupportedOperationException("S3 storage does not support file paths");
    }

    @Override
    public byte[] getFileBytes(String href) throws IOException {
        String fileName = href.substring(href.lastIndexOf("/") + 1);
        return amazonS3Client.getObject(bucketName, fileName).getObjectContent().readAllBytes();
    }

    @Override
    public void deleteFile(String href) {
        String fileName = href.substring(href.lastIndexOf("/") + 1);
        amazonS3Client.deleteObject(bucketName, fileName);
    }
}
