package com.zekiloni.george.workspace.infrastructure.out.persistence.page.repository;

import com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.form.FormSubmissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FormSubmissionJpaRepository extends JpaRepository<FormSubmissionEntity, UUID> {
    List<FormSubmissionEntity> findByFormConfigId(UUID formConfigId);
    List<FormSubmissionEntity> findByFormConfigIdAndStatus(UUID formConfigId, String status);
}

