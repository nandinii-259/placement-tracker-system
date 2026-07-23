package com.placementtracker.placement_tracker_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "offers")
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    private Application application;

    @Column(name = "position_title", nullable = false, length = 150)
    private String positionTitle;

    @Column(name = "salary_ctc", precision = 10, scale = 2)
    private BigDecimal salaryCtc;

    @Column(name = "offer_date", nullable = false)
    private LocalDate offerDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.offerDate == null) {
            this.offerDate = LocalDate.now();
        }
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

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

    public LocalDate getOfferDate() {
        return offerDate;
    }

    public void setOfferDate(LocalDate offerDate) {
        this.offerDate = offerDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}