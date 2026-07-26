package com.placementtracker.placement_tracker_backend.dto;

import jakarta.validation.constraints.NotNull;

public class ApplicationRequestDto {

    @NotNull(message = "Job ID is required")
    private Long jobId;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
}