package com.placementtracker.placement_tracker_backend.dto;

import com.placementtracker.placement_tracker_backend.entity.Application;
import jakarta.validation.constraints.NotNull;

public class StatusUpdateRequestDto {

    @NotNull(message = "New status is required")
    private Application.Status status;

    private String rejectionReason;

    public Application.Status getStatus() {
        return status;
    }

    public void setStatus(Application.Status status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}