package com.placementtracker.placement_tracker_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class JobResponseDto {

    private Long id;
    private Long companyId;
    private String companyName;
    private String title;
    private String description;
    private BigDecimal minCgpa;
    private LocalDate applicationDeadline;
    private LocalDateTime createdAt;

    public JobResponseDto(Long id, Long companyId, String companyName, String title,
                          String description, BigDecimal minCgpa,
                          LocalDate applicationDeadline, LocalDateTime createdAt) {
        this.id = id;
        this.companyId = companyId;
        this.companyName = companyName;
        this.title = title;
        this.description = description;
        this.minCgpa = minCgpa;
        this.applicationDeadline = applicationDeadline;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getMinCgpa() {
        return minCgpa;
    }

    public LocalDate getApplicationDeadline() {
        return applicationDeadline;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}