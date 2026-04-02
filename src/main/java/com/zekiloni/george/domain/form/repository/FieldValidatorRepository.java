package com.zekiloni.george.domain.form.repository;

import com.zekiloni.george.domain.form.entity.FieldValidator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for FieldValidator entity.
 * Provides CRUD operations and custom queries for field validators.
 */
@Repository
public interface FieldValidatorRepository extends JpaRepository<FieldValidator, Long> {

    /**
     * Find all validators for a specific field.
     */
    List<FieldValidator> findByFormFieldId(Long formFieldId);

    /**
     * Find active validators only.
     */
    List<FieldValidator> findByIsActiveTrue();

    /**
     * Find validators by type.
     */
    List<FieldValidator> findByType(String validationType);
}

