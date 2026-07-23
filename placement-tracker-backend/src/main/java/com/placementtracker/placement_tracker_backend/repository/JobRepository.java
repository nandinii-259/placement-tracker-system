package com.placementtracker.placement_tracker_backend.repository;

import com.placementtracker.placement_tracker_backend.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByCompanyId(Long companyId);
}