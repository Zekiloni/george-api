package com.zekiloni.george.attachment.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AttachmentJpaRepository extends JpaRepository<AttachmentEntity, UUID> {
}
