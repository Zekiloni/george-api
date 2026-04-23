package com.zekiloni.george.attachment.infrastructure.storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.*;

@Component
@ConditionalOnProperty(name = "app.storage.type", havingValue = "local", matchIfMissing = true)
public class LocalFileStorageService implements FileStorageService {
    private final Path storageDir = Paths.get("uploads");

    public LocalFileStorageService() throws IOException {
        if (!Files.exists(storageDir)) {
            Files.createDirectories(storageDir);
        }
    }

    @Override
    public String storeFile(MultipartFile file) throws IOException {
        Path targetLocation = storageDir.resolve(System.currentTimeMillis() + "_" + file.getOriginalFilename());

        try (GZIPOutputStream gzipOut = new GZIPOutputStream(Files.newOutputStream(targetLocation))) {
            gzipOut.write(file.getBytes());
        }

        return targetLocation.toString();
    }

    @Override
    public Path getFilePath(String href) {
        return Paths.get(href);
    }

    @Override
    public byte[] getFileBytes(String href) throws IOException {
        Path path = getFilePath(href);

        try (GZIPInputStream gzipIn = new GZIPInputStream(Files.newInputStream(path));
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipIn.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            return outputStream.toByteArray();
        }
    }

    @Override
    public void deleteFile(String href) {
        Path path = getFilePath(href);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + href, e);
        }
    }
}
