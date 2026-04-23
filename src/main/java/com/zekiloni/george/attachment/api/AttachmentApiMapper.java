package com.zekiloni.george.attachment.api;



import com.zekiloni.george.attachment.api.dto.AttachmentDto;
import com.zekiloni.george.attachment.domain.Attachment;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AttachmentApiMapper {
    AttachmentDto toDto(Attachment attachment);
}
