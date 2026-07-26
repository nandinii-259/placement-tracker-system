package com.placementtracker.placement_tracker_backend.controller;

import com.placementtracker.placement_tracker_backend.dto.InterviewRequestDto;
import com.placementtracker.placement_tracker_backend.dto.InterviewResponseDto;
import com.placementtracker.placement_tracker_backend.entity.Interview;
import com.placementtracker.placement_tracker_backend.service.InterviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interviews")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PostMapping("/application/{applicationId}")
    public ResponseEntity<InterviewResponseDto> scheduleInterview(@PathVariable Long applicationId,
                                                                  @Valid @RequestBody InterviewRequestDto requestDto) {
        Interview interview = new Interview();
        interview.setScheduledAt(requestDto.getScheduledAt());
        interview.setMode(requestDto.getMode());
        interview.setLocationOrLink(requestDto.getLocationOrLink());

        Interview savedInterview = interviewService.scheduleInterview(applicationId, interview);

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDto(savedInterview));
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<InterviewResponseDto> getInterviewByApplication(@PathVariable Long applicationId) {
        Interview interview = interviewService.getInterviewByApplication(applicationId);
        return ResponseEntity.ok(toResponseDto(interview));
    }

    private InterviewResponseDto toResponseDto(Interview interview) {
        return new InterviewResponseDto(
                interview.getId(),
                interview.getApplication().getId(),
                interview.getApplication().getStudent().getFullName(),
                interview.getApplication().getJob().getTitle(),
                interview.getScheduledAt(),
                interview.getMode(),
                interview.getLocationOrLink(),
                interview.getOutcome()
        );
    }
}