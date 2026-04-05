//package com.zekiloni.george.domain.form.repository;
//
//import com.zekiloni.george.domain.form.entity.FieldOption;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
///**
// * Repository interface for FieldOption entity.
// * Provides CRUD operations and custom queries for field options.
// */
//@Repository
//public interface FieldOptionRepository extends JpaRepository<FieldOption, Long> {
//
//    /**
//     * Find all options for a specific field.
//     */
//    List<FieldOption> findByFormFieldId(Long formFieldId);
//
//    /**
//     * Find active options only.
//     */
//    List<FieldOption> findByIsActiveTrue();
//
//    /**
//     * Find default option for a field.
//     */
//    List<FieldOption> findByFormFieldIdAndIsDefaultTrue(Long formFieldId);
//
//    /**
//     * Find options ordered by display order.
//     */
//    List<FieldOption> findByFormFieldIdOrderByDisplayOrderAsc(Long formFieldId);
//}
//
