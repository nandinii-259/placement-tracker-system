package com.placementtracker.placement_tracker_backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public class OfferRequestDto {

    @NotBlank(message = "Position title is required")
    private String positionTitle;

    @DecimalMin(value = "0.0", message = "Salary cannot be negative")
    private BigDecimal salaryCtc;

    public String getPositionTitle() {
        return positionTitle;
    }

    public void setPositionTitle(String positionTitle) {
        this.positionTitle = positionTitle;
    }

    public BigDecimal getSalaryCtc() {
        return salaryCtc;
    }

    public void setSalaryCtc(BigDecimal salaryCtc) {
        this.salaryCtc = salaryCtc;
    }
}