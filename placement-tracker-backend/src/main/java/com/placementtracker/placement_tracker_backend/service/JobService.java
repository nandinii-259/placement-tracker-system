package com.placementtracker.placement_tracker_backend.service;

import com.placementtracker.placement_tracker_backend.entity.Company;
import com.placementtracker.placement_tracker_backend.entity.Job;
import com.placementtracker.placement_tracker_backend.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final CompanyService companyService;

    public JobService(JobRepository jobRepository, CompanyService companyService) {
        this.jobRepository = jobRepository;
        this.companyService = companyService;
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public List<Job> getJobsByCompany(Long companyId) {
        return jobRepository.findByCompanyId(companyId);
    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));
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
        jobRepository.delete(job);
    }
}