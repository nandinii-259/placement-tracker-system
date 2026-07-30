package com.placementtracker.placement_tracker_backend.service;

import com.placementtracker.placement_tracker_backend.entity.Company;
import com.placementtracker.placement_tracker_backend.entity.Job;
import com.placementtracker.placement_tracker_backend.exception.BusinessRuleException;
import com.placementtracker.placement_tracker_backend.exception.ResourceNotFoundException;
import com.placementtracker.placement_tracker_backend.repository.ApplicationRepository;
import com.placementtracker.placement_tracker_backend.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final CompanyService companyService;
    private final ApplicationRepository applicationRepository;

    public JobService(JobRepository jobRepository, CompanyService companyService,
                      ApplicationRepository applicationRepository) {
        this.jobRepository = jobRepository;
        this.companyService = companyService;
        this.applicationRepository = applicationRepository;
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public List<Job> getJobsByCompany(Long companyId) {
        return jobRepository.findByCompanyId(companyId);
    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
    }

    public Job createJob(Long companyId, Job job) {
        Company company = companyService.getCompanyById(companyId);
        job.setCompany(company);
        return jobRepository.save(job);
    }

    public Job updateJob(Long id, Job updatedJob) {
        Job existingJob = getJobById(id);
        existingJob.setTitle(updatedJob.getTitle());
        existingJob.setDescription(updatedJob.getDescription());
        existingJob.setMinCgpa(updatedJob.getMinCgpa());
        existingJob.setApplicationDeadline(updatedJob.getApplicationDeadline());
        return jobRepository.save(existingJob);
    }

    public void deleteJob(Long id) {
        Job job = getJobById(id);

        if (!applicationRepository.findByJobId(id).isEmpty()) {
            throw new BusinessRuleException(
                    "Cannot delete this job because it has existing applications.");
        }

        jobRepository.delete(job);
    }
}