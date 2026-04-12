package com.zekiloni.george.workspace.infrastructure.out.persistence.page.mapper;

import com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.form.FormSubmissionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FormSubmissionEntityMapper {

//    @Mapping(target = "form", ignore = true)
//    FormSubmissionEntity toEntity(FormSubmission domain);
//
//    @Mapping(source = "formConfig.id", target = "formConfigId")
//    FormSubmission toDomain(FormSubmissionEntity entity);
}

