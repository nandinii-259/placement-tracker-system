package com.placementtracker.placement_tracker_backend.service;

import com.placementtracker.placement_tracker_backend.entity.Application;
import com.placementtracker.placement_tracker_backend.entity.Job;
import com.placementtracker.placement_tracker_backend.exception.BusinessRuleException;
import com.placementtracker.placement_tracker_backend.repository.ApplicationRepository;
import com.placementtracker.placement_tracker_backend.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private CompanyService companyService;

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private JobService jobService;

    @Test
    void deleteJob_shouldThrowException_whenApplicationsExist() {
        Job job = new Job();
        job.setId(1L);

        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(applicationRepository.findByJobId(1L)).thenReturn(List.of(new Application()));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class,
                () -> jobService.deleteJob(1L));

        assertEquals("Cannot delete this job because it has existing applications.", exception.getMessage());
        verify(jobRepository, never()).delete(any());
    }

    @Test
    void deleteJob_shouldSucceed_whenNoApplicationsExist() {
        Job job = new Job();
        job.setId(1L);

        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(applicationRepository.findByJobId(1L)).thenReturn(List.of());

        jobService.deleteJob(1L);

        verify(jobRepository, times(1)).delete(job);
    }
}