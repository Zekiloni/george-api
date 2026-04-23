package com.zekiloni.george.attachment.application;

import com.zekiloni.george.attachment.domain.Attachment;
import com.zekiloni.george.attachment.domain.AttachmentRepository;
import com.zekiloni.george.attachment.infrastructure.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;


@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class AttachmentService {
    private final AttachmentRepository repository;
    private final FileStorageService storageService;

    @Transactional
    public Attachment createAttachment(MultipartFile file, String type, String description) throws IOException {
        String mimeType = file.getContentType();
        if (mimeType == null) {
            mimeType = Files.probeContentType(Path.of(Objects.requireNonNull(file.getOriginalFilename())));
        }

        String href = storageService.storeFile(file);
        Attachment attachment = Attachment.builder()
                .fileName(file.getOriginalFilename())
                .mimeType(mimeType)
                .size(file.getSize())
                .type(type)
                .description(description)
                .href(href)
                .build();

        return repository.save(attachment);
    }

    public void deleteAttachment(UUID id) {
        repository.findById(id).ifPresent(attachment -> {
            storageService.deleteFile(attachment.getHref());
            repository.delete(attachment);
        });
    }

    public Attachment getAttachmentById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public byte[] getAttachmentFile(String href) throws IOException {
        return storageService.getFileBytes(href);
    }
}
