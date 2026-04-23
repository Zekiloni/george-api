package com.zekiloni.george.attachment.domain;

import java.util.Optional;
import java.util.UUID;

public interface AttachmentRepository {
    Optional<Attachment> findById(UUID id);
    void delete(Attachment attachment);
    Attachment save(Attachment attachment);
}