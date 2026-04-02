package com.zekiloni.george.domain.form.repository;

import com.zekiloni.george.domain.form.entity.FormField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for FormField entity.
 * Provides CRUD operations and custom queries for form fields.
 */
@Repository
public interface FormFieldRepository extends JpaRepository<FormField, Long> {

    /**
     * Find all fields for a specific form configuration.
     */
    List<FormField> findByFormConfigId(Long formConfigId);

    /**
     * Find all fields by their type.
     */
    List<FormField> findByType(String fieldType);

    /**
     * Find active fields only.
     */
    List<FormField> findByIsActiveTrue();

    /**
     * Find required fields.
     */
    List<FormField> findByIsRequiredTrue();
}

