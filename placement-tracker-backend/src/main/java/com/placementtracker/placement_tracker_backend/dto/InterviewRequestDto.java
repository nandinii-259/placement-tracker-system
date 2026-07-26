package com.placementtracker.placement_tracker_backend.dto;

import com.placementtracker.placement_tracker_backend.entity.Interview;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class InterviewRequestDto {

    @NotNull(message = "Scheduled date and time is required")
    @Future(message = "Interview must be scheduled in the future")
    private LocalDateTime scheduledAt;

    @NotNull(message = "Interview mode is required")
    private Interview.Mode mode;

    private String locationOrLink;

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public Interview.Mode getMode() {
        return mode;
    }

    public void setMode(Interview.Mode mode) {
        this.mode = mode;
    }

    public String getLocationOrLink() {
        return locationOrLink;
    }

    public void setLocationOrLink(String locationOrLink) {
        this.locationOrLink = locationOrLink;
    }
}