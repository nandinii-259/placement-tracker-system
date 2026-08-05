package com.placementtracker.placement_tracker_backend.service;

import com.placementtracker.placement_tracker_backend.dto.AnalyticsResponseDto;
import com.placementtracker.placement_tracker_backend.dto.CompanyOfferCountDto;
import com.placementtracker.placement_tracker_backend.entity.Application;
import com.placementtracker.placement_tracker_backend.repository.ApplicationRepository;
import com.placementtracker.placement_tracker_backend.repository.CompanyRepository;
import com.placementtracker.placement_tracker_backend.repository.JobRepository;
import com.placementtracker.placement_tracker_backend.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    public AnalyticsService(StudentRepository studentRepository, CompanyRepository companyRepository,
                            JobRepository jobRepository, ApplicationRepository applicationRepository) {
        this.studentRepository = studentRepository;
        this.companyRepository = companyRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
    }

    public AnalyticsResponseDto getAnalytics() {
        long totalStudents = studentRepository.count();
        long totalCompanies = companyRepository.count();
        long totalJobs = jobRepository.count();
        long totalApplications = applicationRepository.count();

        List<Application> offeredApplications = applicationRepository.findByStatus(Application.Status.OFFERED);

        long studentsPlaced = offeredApplications.stream()
                .map(app -> app.getStudent().getId())
                .distinct()
                .count();

        double placementRate = totalStudents == 0 ? 0.0 : (studentsPlaced * 100.0 / totalStudents);

        Map<String, Long> groupedByCompany = offeredApplications.stream()
                .collect(Collectors.groupingBy(
                        app -> app.getJob().getCompany().getName(),
                        Collectors.counting()
                ));

        List<CompanyOfferCountDto> offersByCompany = groupedByCompany.entrySet().stream()
                .map(entry -> new CompanyOfferCountDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return new AnalyticsResponseDto(
                totalStudents, totalCompanies, totalJobs, totalApplications,
                studentsPlaced, Math.round(placementRate * 10.0) / 10.0, offersByCompany
        );
    }
}