package com.placementtracker.placement_tracker_backend.service;

import com.placementtracker.placement_tracker_backend.entity.Application;
import com.placementtracker.placement_tracker_backend.entity.Interview;
import com.placementtracker.placement_tracker_backend.repository.InterviewRepository;
import org.springframework.stereotype.Service;

@Service
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final ApplicationService applicationService;

    public InterviewService(InterviewRepository interviewRepository, ApplicationService applicationService) {
        this.interviewRepository = interviewRepository;
        this.applicationService = applicationService;
    }

    public Interview scheduleInterview(Long applicationId, Interview interview) {
        Application application = applicationService.getApplicationById(applicationId);

        if (application.getStatus() != Application.Status.SHORTLISTED) {
            throw new IllegalStateException(
                    "An interview can only be scheduled for a SHORTLISTED application.");
        }

        if (interviewRepository.findByApplicationId(applicationId).isPresent()) {
            throw new IllegalStateException("An interview has already been scheduled for this application.");
        }

        interview.setApplication(application);
        Interview savedInterview = interviewRepository.save(interview);

        applicationService.updateStatus(applicationId, Application.Status.INTERVIEW_SCHEDULED, null);

        return savedInterview;
    }

    public Interview getInterviewByApplication(Long applicationId) {
        return interviewRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new RuntimeException("No interview found for this application."));
    }
}