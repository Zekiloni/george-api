package com.zekiloni.george.domain.form.repository;

import com.zekiloni.george.domain.form.entity.FormConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for FormConfig entity.
 * Provides CRUD operations and custom queries for form configurations.
 */
@Repository
public interface FormConfigRepository extends JpaRepository<FormConfig, Long> {

    /**
     * Find a form configuration by its unique name.
     */
    Optional<FormConfig> findByFormName(String formName);

    /**
     * Find all active forms.
     */
    List<FormConfig> findByIsActiveTrue();

    /**
     * Find all public forms.
     */
    List<FormConfig> findByIsPublicTrue();

    /**
     * Find all forms that are both active and public.
     */
    List<FormConfig> findByIsActiveTrueAndIsPublicTrue();

    /**
     * Find forms created by a specific user.
     */
    List<FormConfig> findByCreatedBy(String createdBy);

    /**
     * Check if a form name already exists.
     */
    boolean existsByFormName(String formName);
}

