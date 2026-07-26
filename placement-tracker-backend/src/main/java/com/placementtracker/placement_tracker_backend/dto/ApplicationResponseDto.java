package com.placementtracker.placement_tracker_backend.dto;

import com.placementtracker.placement_tracker_backend.entity.Application;

import java.time.LocalDateTime;

public class ApplicationResponseDto {

    private Long id;
    private Long studentId;
    private String studentName;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private Application.Status status;
    private String rejectionReason;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;

    public ApplicationResponseDto(Long id, Long studentId, String studentName, Long jobId,
                                  String jobTitle, String companyName, Application.Status status,
                                  String rejectionReason, LocalDateTime appliedAt, LocalDateTime updatedAt) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.status = status;
        this.rejectionReason = rejectionReason;
        this.appliedAt = appliedAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public Long getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public Long getJobId() { return jobId; }
    public String getJobTitle() { return jobTitle; }
    public String getCompanyName() { return companyName; }
    public Application.Status getStatus() { return status; }
    public String getRejectionReason() { return rejectionReason; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}