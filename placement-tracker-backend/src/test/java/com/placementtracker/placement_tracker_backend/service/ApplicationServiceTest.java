package com.placementtracker.placement_tracker_backend.service;

import com.placementtracker.placement_tracker_backend.entity.Application;
import com.placementtracker.placement_tracker_backend.entity.Job;
import com.placementtracker.placement_tracker_backend.entity.Student;
import com.placementtracker.placement_tracker_backend.exception.BusinessRuleException;
import com.placementtracker.placement_tracker_backend.repository.ApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private StudentService studentService;

    @Mock
    private JobService jobService;

    @InjectMocks
    private ApplicationService applicationService;

    private Student student;
    private Job job;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId(1L);
        student.setCgpa(new BigDecimal("8.00"));

        job = new Job();
        job.setId(1L);
        job.setMinCgpa(new BigDecimal("7.00"));
        job.setApplicationDeadline(LocalDate.now().plusDays(10));
    }

    @Test
    void applyToJob_shouldThrowException_whenAlreadyApplied() {
        when(studentService.getStudentById(1L)).thenReturn(student);
        when(jobService.getJobById(1L)).thenReturn(job);
        when(applicationRepository.existsByStudentIdAndJobId(1L, 1L)).thenReturn(true);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class,
                () -> applicationService.applyToJob(1L, 1L));

        assertEquals("You have already applied to this job.", exception.getMessage());
        verify(applicationRepository, never()).save(any());
    }

    @Test
    void applyToJob_shouldThrowException_whenDeadlinePassed() {
        job.setApplicationDeadline(LocalDate.now().minusDays(1));

        when(studentService.getStudentById(1L)).thenReturn(student);
        when(jobService.getJobById(1L)).thenReturn(job);
        when(applicationRepository.existsByStudentIdAndJobId(1L, 1L)).thenReturn(false);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class,
                () -> applicationService.applyToJob(1L, 1L));

        assertEquals("The application deadline for this job has passed.", exception.getMessage());
    }

    @Test
    void applyToJob_shouldThrowException_whenCgpaTooLow() {
        student.setCgpa(new BigDecimal("6.00"));

        when(studentService.getStudentById(1L)).thenReturn(student);
        when(jobService.getJobById(1L)).thenReturn(job);
        when(applicationRepository.existsByStudentIdAndJobId(1L, 1L)).thenReturn(false);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class,
                () -> applicationService.applyToJob(1L, 1L));

        assertEquals("You do not meet the minimum CGPA requirement for this job.", exception.getMessage());
    }

    @Test
    void applyToJob_shouldSucceed_whenAllConditionsAreMet() {
        when(studentService.getStudentById(1L)).thenReturn(student);
        when(jobService.getJobById(1L)).thenReturn(job);
        when(applicationRepository.existsByStudentIdAndJobId(1L, 1L)).thenReturn(false);
        when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Application result = applicationService.applyToJob(1L, 1L);

        assertNotNull(result);
        assertEquals(Application.Status.APPLIED, result.getStatus());
        assertEquals(student, result.getStudent());
        assertEquals(job, result.getJob());
        verify(applicationRepository, times(1)).save(any(Application.class));
    }
    @Test
    void updateStatus_shouldSucceed_forValidTransition() {
        Application application = new Application();
        application.setId(1L);
        application.setStatus(Application.Status.UNDER_REVIEW);


        when(applicationRepository.findById(1L)).thenReturn(java.util.Optional.of(application));
        when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Application result = applicationService.updateStatus(1L, Application.Status.SHORTLISTED, null);

        assertEquals(Application.Status.SHORTLISTED, result.getStatus());
    }

    @Test
    void updateStatus_shouldThrowException_forInvalidTransition() {
        Application application = new Application();
        application.setId(1L);
        application.setStatus(Application.Status.APPLIED);

        when(applicationRepository.findById(1L)).thenReturn(java.util.Optional.of(application));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class,
                () -> applicationService.updateStatus(1L, Application.Status.OFFERED, null));

        assertTrue(exception.getMessage().contains("Cannot move application"));
    }

    @Test
    void updateStatus_shouldThrowException_whenRejectingFromShortlistedWithoutReason() {
        Application application = new Application();
        application.setId(1L);
        application.setStatus(Application.Status.SHORTLISTED);

        when(applicationRepository.findById(1L)).thenReturn(java.util.Optional.of(application));

        assertThrows(BusinessRuleException.class,
                () -> applicationService.updateStatus(1L, Application.Status.REJECTED, ""));
    }

    @Test
    void updateStatus_shouldSucceed_whenRejectingFromShortlistedWithReason() {
        Application application = new Application();
        application.setId(1L);
        application.setStatus(Application.Status.SHORTLISTED);

        when(applicationRepository.findById(1L)).thenReturn(java.util.Optional.of(application));
        when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Application result = applicationService.updateStatus(1L, Application.Status.REJECTED, "Did not clear the technical round");

        assertEquals(Application.Status.REJECTED, result.getStatus());
        assertEquals("Did not clear the technical round", result.getRejectionReason());
    }
}