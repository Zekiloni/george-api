package com.zekiloni.george.platform.infrastructure.out.persistence.page.repository;

import com.zekiloni.george.platform.domain.model.page.TemplateSource;
import com.zekiloni.george.platform.infrastructure.out.persistence.page.entity.PageTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PageTemplateJpaRepository extends JpaRepository<PageTemplateEntity, UUID> {
    Optional<PageTemplateEntity> findByTitleAndSource(String title, TemplateSource source);
}
