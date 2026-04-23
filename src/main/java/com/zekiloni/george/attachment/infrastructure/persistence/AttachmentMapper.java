package com.zekiloni.george.attachment.infrastructure.persistence;

import com.zekiloni.george.attachment.domain.Attachment;
import org.mapstruct.Mapper;

@Mapper
public interface AttachmentMapper {
    AttachmentEntity toEntity(Attachment attachment);

    Attachment toDomain(AttachmentEntity entity);
}
