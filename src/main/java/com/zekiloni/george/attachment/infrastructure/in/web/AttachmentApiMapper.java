package com.zekiloni.george.attachment.infrastructure.in.web;



import com.zekiloni.george.attachment.infrastructure.in.web.dto.AttachmentDto;
import com.zekiloni.george.attachment.domain.Attachment;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AttachmentApiMapper {
    AttachmentDto toDto(Attachment attachment);
}
