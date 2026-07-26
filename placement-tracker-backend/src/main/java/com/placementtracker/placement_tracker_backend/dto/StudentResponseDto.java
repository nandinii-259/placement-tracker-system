package com.placementtracker.placement_tracker_backend.dto;

import java.math.BigDecimal;

public class StudentResponseDto {

    private Long id;
    private String email;
    private String fullName;
    private String branch;
    private BigDecimal cgpa;
    private Integer graduationYear;

    public StudentResponseDto(Long id, String email, String fullName, String branch,
                              BigDecimal cgpa, Integer graduationYear) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.branch = branch;
        this.cgpa = cgpa;
        this.graduationYear = graduationYear;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getBranch() { return branch; }
    public BigDecimal getCgpa() { return cgpa; }
    public Integer getGraduationYear() { return graduationYear; }
}