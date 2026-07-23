package com.placementtracker.placement_tracker_backend.repository;

import com.placementtracker.placement_tracker_backend.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterviewRepository extends JpaRepository<Interview, Long> {

    Optional<Interview> findByApplicationId(Long applicationId);
}