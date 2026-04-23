package com.zekiloni.george.attachment.infrastructure.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface FileStorageService {
    String storeFile(MultipartFile file) throws IOException;
    Path getFilePath(String href);
    byte[] getFileBytes(String href) throws IOException;
    void deleteFile(String href);
}
