package com.placementtracker.placement_tracker_backend.service;

import com.placementtracker.placement_tracker_backend.entity.Application;
import com.placementtracker.placement_tracker_backend.entity.Job;
import com.placementtracker.placement_tracker_backend.entity.Student;
import com.placementtracker.placement_tracker_backend.repository.ApplicationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final StudentService studentService;
    private final JobService jobService;

    public ApplicationService(ApplicationRepository applicationRepository,
                              StudentService studentService,
                              JobService jobService) {
        this.applicationRepository = applicationRepository;
        this.studentService = studentService;
        this.jobService = jobService;
    }

    private static final Map<Application.Status, Set<Application.Status>> VALID_TRANSITIONS = Map.of(
            Application.Status.APPLIED, Set.of(Application.Status.UNDER_REVIEW),
            Application.Status.UNDER_REVIEW, Set.of(Application.Status.SHORTLISTED, Application.Status.REJECTED),
            Application.Status.SHORTLISTED, Set.of(Application.Status.INTERVIEW_SCHEDULED, Application.Status.REJECTED),
            Application.Status.INTERVIEW_SCHEDULED, Set.of(Application.Status.SELECTED, Application.Status.REJECTED),
            Application.Status.SELECTED, Set.of(Application.Status.OFFERED)
    );

    public Application applyToJob(Long studentId, Long jobId) {
        Student student = studentService.getStudentById(studentId);
        Job job = jobService.getJobById(jobId);

        if (applicationRepository.existsByStudentIdAndJobId(studentId, jobId)) {
            throw new IllegalStateException("You have already applied to this job.");
        }

        if (job.getApplicationDeadline().isBefore(LocalDate.now())) {
            throw new IllegalStateException("The application deadline for this job has passed.");
        }

        if (student.getCgpa().compareTo(job.getMinCgpa()) < 0) {
            throw new IllegalStateException("You do not meet the minimum CGPA requirement for this job.");
        }

        Application application = new Application();
        application.setStudent(student);
        application.setJob(job);
        application.setStatus(Application.Status.APPLIED);

        return applicationRepository.save(application);
    }

    public Application updateStatus(Long applicationId, Application.Status newStatus, String rejectionReason) {
        Application application = getApplicationById(applicationId);
        Application.Status currentStatus = application.getStatus();

        Set<Application.Status> allowedNextStatuses = VALID_TRANSITIONS.get(currentStatus);
        if (allowedNextStatuses == null || !allowedNextStatuses.contains(newStatus)) {
            throw new IllegalStateException(
                    "Cannot move application from " + currentStatus + " to " + newStatus);
        }

        boolean isRejectionRequiringReason =
                newStatus == Application.Status.REJECTED &&
                        (currentStatus == Application.Status.SHORTLISTED ||
                                currentStatus == Application.Status.INTERVIEW_SCHEDULED);

        if (isRejectionRequiringReason && (rejectionReason == null || rejectionReason.isBlank())) {
            throw new IllegalArgumentException(
                    "A rejection reason is required when rejecting a shortlisted or interviewed application.");
        }

        application.setStatus(newStatus);
        if (newStatus == Application.Status.REJECTED) {
            application.setRejectionReason(rejectionReason);
        }

        return applicationRepository.save(application);
    }

    public Application getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found with id: " + id));
    }

    public List<Application> getApplicationsByStudent(Long studentId) {
        return applicationRepository.findByStudentId(studentId);
    }

    public List<Application> getApplicationsByJob(Long jobId) {
        return applicationRepository.findByJobId(jobId);
    }
}