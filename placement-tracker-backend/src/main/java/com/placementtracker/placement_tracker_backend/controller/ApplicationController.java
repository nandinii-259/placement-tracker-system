package com.placementtracker.placement_tracker_backend.controller;

import com.placementtracker.placement_tracker_backend.dto.ApplicationRequestDto;
import com.placementtracker.placement_tracker_backend.dto.ApplicationResponseDto;
import com.placementtracker.placement_tracker_backend.dto.StatusUpdateRequestDto;
import com.placementtracker.placement_tracker_backend.entity.Application;
import com.placementtracker.placement_tracker_backend.entity.Student;
import com.placementtracker.placement_tracker_backend.exception.BusinessRuleException;
import com.placementtracker.placement_tracker_backend.service.ApplicationService;
import com.placementtracker.placement_tracker_backend.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final StudentService studentService;

    public ApplicationController(ApplicationService applicationService, StudentService studentService) {
        this.applicationService = applicationService;
        this.studentService = studentService;
    }

    @PostMapping("/student/{studentId}")
    public ResponseEntity<ApplicationResponseDto> applyToJob(@PathVariable Long studentId,
                                                             @Valid @RequestBody ApplicationRequestDto requestDto) {
        verifyStudentOwnership(studentId);
        Application application = applicationService.applyToJob(studentId, requestDto.getJobId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDto(application));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponseDto> getApplicationById(@PathVariable Long id) {
        Application application = applicationService.getApplicationById(id);

        if (!isAdmin()) {
            verifyStudentOwnership(application.getStudent().getId());
        }

        return ResponseEntity.ok(toResponseDto(application));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ApplicationResponseDto>> getApplicationsByStudent(@PathVariable Long studentId) {
        if (!isAdmin()) {
            verifyStudentOwnership(studentId);
        }

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

    private void verifyStudentOwnership(Long requestedStudentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loggedInEmail = auth.getName();

        Student loggedInStudent = studentService.getStudentByEmail(loggedInEmail);

        if (!loggedInStudent.getId().equals(requestedStudentId)) {
            throw new BusinessRuleException("You are not allowed to access another student's data.");
        }
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
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