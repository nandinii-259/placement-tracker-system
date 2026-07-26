package com.placementtracker.placement_tracker_backend.controller;

import com.placementtracker.placement_tracker_backend.dto.JobRequestDto;
import com.placementtracker.placement_tracker_backend.dto.JobResponseDto;
import com.placementtracker.placement_tracker_backend.entity.Job;
import com.placementtracker.placement_tracker_backend.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public ResponseEntity<List<JobResponseDto>> getAllJobs() {
        List<JobResponseDto> jobs = jobService.getAllJobs()
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponseDto> getJobById(@PathVariable Long id) {
        Job job = jobService.getJobById(id);
        return ResponseEntity.ok(toResponseDto(job));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<JobResponseDto>> getJobsByCompany(@PathVariable Long companyId) {
        List<JobResponseDto> jobs = jobService.getJobsByCompany(companyId)
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(jobs);
    }

    @PostMapping("/company/{companyId}")
    public ResponseEntity<JobResponseDto> createJob(@PathVariable Long companyId,
                                                    @Valid @RequestBody JobRequestDto requestDto) {
        Job job = new Job();
        job.setTitle(requestDto.getTitle());
        job.setDescription(requestDto.getDescription());
        job.setMinCgpa(requestDto.getMinCgpa());
        job.setApplicationDeadline(requestDto.getApplicationDeadline());

        Job savedJob = jobService.createJob(companyId, job);

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDto(savedJob));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobResponseDto> updateJob(@PathVariable Long id,
                                                    @Valid @RequestBody JobRequestDto requestDto) {
        Job job = new Job();
        job.setTitle(requestDto.getTitle());
        job.setDescription(requestDto.getDescription());
        job.setMinCgpa(requestDto.getMinCgpa());
        job.setApplicationDeadline(requestDto.getApplicationDeadline());

        Job updatedJob = jobService.updateJob(id, job);

        return ResponseEntity.ok(toResponseDto(updatedJob));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }

    private JobResponseDto toResponseDto(Job job) {
        return new JobResponseDto(
                job.getId(),
                job.getCompany().getId(),
                job.getCompany().getName(),
                job.getTitle(),
                job.getDescription(),
                job.getMinCgpa(),
                job.getApplicationDeadline(),
                job.getCreatedAt()
        );
    }
}