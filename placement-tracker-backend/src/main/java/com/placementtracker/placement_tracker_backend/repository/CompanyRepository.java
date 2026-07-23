package com.placementtracker.placement_tracker_backend.repository;

import com.placementtracker.placement_tracker_backend.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}