package com.placementtracker.placement_tracker_backend.dto;

import java.time.LocalDateTime;

public class CompanyResponseDto {

    private Long id;
    private String name;
    private String description;
    private String website;
    private LocalDateTime createdAt;

    public CompanyResponseDto(Long id, String name, String description, String website, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.website = website;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getWebsite() {
        return website;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}