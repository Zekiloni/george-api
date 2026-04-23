package com.zekiloni.george.attachment.infrastructure.persistence;

import com.zekiloni.george.attachment.domain.Attachment;
import com.zekiloni.george.attachment.domain.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AttachmentRepositoryImpl implements AttachmentRepository {
    private final AttachmentMapper mapper;
    private final AttachmentJpaRepository jpaRepository;

    @Override
    public Attachment save(Attachment attachment) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(attachment)));
    }

    @Override
    public Optional<Attachment> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void delete(Attachment attachment) {
        jpaRepository.deleteById(attachment.getId());
    }
}
