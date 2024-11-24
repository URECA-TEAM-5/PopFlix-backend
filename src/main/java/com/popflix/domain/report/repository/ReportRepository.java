package com.popflix.domain.report.repository;

import com.popflix.domain.report.entity.Report;
import com.popflix.domain.report.enums.ReportStatus;
import com.popflix.domain.report.enums.ReportTarget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    @Query("SELECT r FROM Report r " +
            "WHERE r.id = :reportId " +
            "AND r.isDeleted = false")
    Optional<Report> findActiveById(@Param("reportId") Long reportId);

    @Query("SELECT r FROM Report r " +
            "WHERE r.status = :status " +
            "AND r.isDeleted = false")
    List<Report> findAllActiveByStatus(@Param("status") ReportStatus status);

    @Query("SELECT r FROM Report r " +
            "WHERE r.status = :status " +
            "AND r.isDeleted = false")
    Page<Report> findPageActiveByStatus(
            @Param("status") ReportStatus status,
            Pageable pageable
    );

    @Query("SELECT r FROM Report r " +
            "WHERE r.isDeleted = false " +
            "ORDER BY r.createAt DESC")
    List<Report> findAllActiveOrderByCreatedAtDesc();

    @Query("SELECT r FROM Report r " +
            "WHERE r.isDeleted = false " +
            "ORDER BY r.createAt DESC")
    Page<Report> findPageActiveOrderByCreatedAtDesc(Pageable pageable);

    // 특정 타겟에 대한 신고 목록 조회
    @Query("SELECT r FROM Report r " +
            "WHERE r.targetType = :targetType " +
            "AND r.targetId = :targetId " +
            "AND r.isDeleted = false")
    List<Report> findAllByTargetTypeAndTargetId(
            @Param("targetType") ReportTarget targetType,
            @Param("targetId") Long targetId
    );

    @Query("SELECT r FROM Report r " +
            "WHERE r.reporter.userId = :reporterId " +
            "AND r.isDeleted = false")
    List<Report> findAllByReporterId(@Param("reporterId") Long reporterId);

    @Query("SELECT COUNT(r) FROM Report r " +
            "WHERE r.targetType = :targetType " +
            "AND r.targetId = :targetId " +
            "AND r.isDeleted = false")
    long countActiveByTargetTypeAndTargetId(
            @Param("targetType") ReportTarget targetType,
            @Param("targetId") Long targetId
    );

    @Query("SELECT COUNT(r) FROM Report r " +
            "WHERE r.status = :status " +
            "AND r.isDeleted = false")
    long countActiveByStatus(@Param("status") ReportStatus status);

    // 사용자의 특정 타겟 신고 여부 확인
    @Query("SELECT COUNT(r) > 0 FROM Report r " +
            "WHERE r.reporter.userId = :reporterId " +
            "AND r.targetType = :targetType " +
            "AND r.targetId = :targetId " +
            "AND r.isDeleted = false")
    boolean existsActiveByReporterIdAndTargetTypeAndTargetId(
            @Param("reporterId") Long reporterId,
            @Param("targetType") ReportTarget targetType,
            @Param("targetId") Long targetId
    );
}