package com.placementtracker.placement_tracker_backend.controller;

import com.placementtracker.placement_tracker_backend.dto.ApplicationRequestDto;
import com.placementtracker.placement_tracker_backend.dto.ApplicationResponseDto;
import com.placementtracker.placement_tracker_backend.dto.StatusUpdateRequestDto;
import com.placementtracker.placement_tracker_backend.entity.Application;
import com.placementtracker.placement_tracker_backend.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/student/{studentId}")
    public ResponseEntity<ApplicationResponseDto> applyToJob(@PathVariable Long studentId,
                                                             @Valid @RequestBody ApplicationRequestDto requestDto) {
        Application application = applicationService.applyToJob(studentId, requestDto.getJobId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDto(application));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponseDto> getApplicationById(@PathVariable Long id) {
        Application application = applicationService.getApplicationById(id);
        return ResponseEntity.ok(toResponseDto(application));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ApplicationResponseDto>> getApplicationsByStudent(@PathVariable Long studentId) {
        List<ApplicationResponseDto> applications = applicationService.getApplicationsByStudent(studentId)
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(applications);
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<ApplicationResponseDto>> getApplicationsByJob(@PathVariable Long jobId) {
        List<ApplicationResponseDto> applications = applicationService.getApplicationsByJob(jobId)
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(applications);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApplicationResponseDto> updateStatus(@PathVariable Long id,
                                                               @Valid @RequestBody StatusUpdateRequestDto requestDto) {
        Application application = applicationService.updateStatus(
                id, requestDto.getStatus(), requestDto.getRejectionReason());

        return ResponseEntity.ok(toResponseDto(application));
    }

    private ApplicationResponseDto toResponseDto(Application application) {
        return new ApplicationResponseDto(
                application.getId(),
                application.getStudent().getId(),
                application.getStudent().getFullName(),
                application.getJob().getId(),
                application.getJob().getTitle(),
                application.getJob().getCompany().getName(),
                application.getStatus(),
                application.getRejectionReason(),
                application.getAppliedAt(),
                application.getUpdatedAt()
        );
    }
}