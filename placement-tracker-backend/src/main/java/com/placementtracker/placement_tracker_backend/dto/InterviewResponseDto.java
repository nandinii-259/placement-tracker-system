package com.placementtracker.placement_tracker_backend.dto;

import com.placementtracker.placement_tracker_backend.entity.Interview;

import java.time.LocalDateTime;

public class InterviewResponseDto {

    private Long id;
    private Long applicationId;
    private String studentName;
    private String jobTitle;
    private LocalDateTime scheduledAt;
    private Interview.Mode mode;
    private String locationOrLink;
    private Interview.Outcome outcome;

    public InterviewResponseDto(Long id, Long applicationId, String studentName, String jobTitle,
                                LocalDateTime scheduledAt, Interview.Mode mode,
                                String locationOrLink, Interview.Outcome outcome) {
        this.id = id;
        this.applicationId = applicationId;
        this.studentName = studentName;
        this.jobTitle = jobTitle;
        this.scheduledAt = scheduledAt;
        this.mode = mode;
        this.locationOrLink = locationOrLink;
        this.outcome = outcome;
    }

    public Long getId() { return id; }
    public Long getApplicationId() { return applicationId; }
    public String getStudentName() { return studentName; }
    public String getJobTitle() { return jobTitle; }
    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public Interview.Mode getMode() { return mode; }
    public String getLocationOrLink() { return locationOrLink; }
    public Interview.Outcome getOutcome() { return outcome; }
}