package com.placementtracker.placement_tracker_backend.controller;

import com.placementtracker.placement_tracker_backend.dto.CompanyRequestDto;
import com.placementtracker.placement_tracker_backend.dto.CompanyResponseDto;
import com.placementtracker.placement_tracker_backend.entity.Company;
import com.placementtracker.placement_tracker_backend.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public ResponseEntity<List<CompanyResponseDto>> getAllCompanies() {
        List<CompanyResponseDto> companies = companyService.getAllCompanies()
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(companies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponseDto> getCompanyById(@PathVariable Long id) {
        Company company = companyService.getCompanyById(id);
        return ResponseEntity.ok(toResponseDto(company));
    }

    @PostMapping
    public ResponseEntity<CompanyResponseDto> createCompany(@Valid @RequestBody CompanyRequestDto requestDto) {
        Company company = new Company();
        company.setName(requestDto.getName());
        company.setDescription(requestDto.getDescription());
        company.setWebsite(requestDto.getWebsite());

        Company savedCompany = companyService.createCompany(company);

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDto(savedCompany));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponseDto> updateCompany(@PathVariable Long id,
                                                            @Valid @RequestBody CompanyRequestDto requestDto) {
        Company company = new Company();
        company.setName(requestDto.getName());
        company.setDescription(requestDto.getDescription());
        company.setWebsite(requestDto.getWebsite());

        Company updatedCompany = companyService.updateCompany(id, company);

        return ResponseEntity.ok(toResponseDto(updatedCompany));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }

    private CompanyResponseDto toResponseDto(Company company) {
        return new CompanyResponseDto(
                company.getId(),
                company.getName(),
                company.getDescription(),
                company.getWebsite(),
                company.getCreatedAt()
        );
    }
}