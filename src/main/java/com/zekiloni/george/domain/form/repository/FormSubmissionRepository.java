//package com.zekiloni.george.domain.form.repository;
//
//import com.zekiloni.george.domain.form.entity.FormSubmission;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
///**
// * Repository interface for FormSubmission entity.
// * Provides CRUD operations and custom queries for form submissions.
// */
//@Repository
//public interface FormSubmissionRepository extends JpaRepository<FormSubmission, Long> {
//
//    /**
//     * Find all submissions for a specific form configuration.
//     */
//    List<FormSubmission> findByFormConfigId(Long formConfigId);
//
//    /**
//     * Find submissions by status.
//     */
//    List<FormSubmission> findByStatus(String status);
//
//    /**
//     * Find submissions within a date range.
//     */
//    List<FormSubmission> findBySubmittedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
//
//    /**
//     * Find submissions by a specific user.
//     */
//    List<FormSubmission> findBySubmittedBy(String submittedBy);
//
//    /**
//     * Find submissions for a form by status.
//     */
//    List<FormSubmission> findByFormConfigIdAndStatus(Long formConfigId, String status);
//
//    /**
//     * Count total submissions for a form.
//     */
//    long countByFormConfigId(Long formConfigId);
//
//    /**
//     * Count submissions by status for a form.
//     */
//    long countByFormConfigIdAndStatus(Long formConfigId, String status);
//}
//
