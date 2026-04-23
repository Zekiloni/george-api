package com.zekiloni.george.attachment.infrastructure.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface AttachmentJpaRepository extends CrudRepository<AttachmentEntity, UUID> {
}
