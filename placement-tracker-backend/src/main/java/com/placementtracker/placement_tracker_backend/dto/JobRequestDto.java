package com.placementtracker.placement_tracker_backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class JobRequestDto {

    @NotBlank(message = "Job title is required")
    private String title;

    private String description;

    @NotNull(message = "Minimum CGPA is required")
    @DecimalMin(value = "0.0", message = "Minimum CGPA cannot be negative")
    private java.math.BigDecimal minCgpa;

    @NotNull(message = "Application deadline is required")
    @Future(message = "Application deadline must be in the future")
    private LocalDate applicationDeadline;

    // Getters and Setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public java.math.BigDecimal getMinCgpa() {
        return minCgpa;
    }

    public void setMinCgpa(java.math.BigDecimal minCgpa) {
        this.minCgpa = minCgpa;
    }

    public LocalDate getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(LocalDate applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }
}