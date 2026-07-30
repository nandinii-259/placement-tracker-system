package com.placementtracker.placement_tracker_backend.service;

import com.placementtracker.placement_tracker_backend.entity.Company;
import com.placementtracker.placement_tracker_backend.exception.BusinessRuleException;
import com.placementtracker.placement_tracker_backend.exception.ResourceNotFoundException;
import com.placementtracker.placement_tracker_backend.repository.CompanyRepository;
import com.placementtracker.placement_tracker_backend.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;

    public CompanyService(CompanyRepository companyRepository, JobRepository jobRepository) {
        this.companyRepository = companyRepository;
        this.jobRepository = jobRepository;
    }

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public Company getCompanyById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
    }

    public Company createCompany(Company company) {
        return companyRepository.save(company);
    }

    public Company updateCompany(Long id, Company updatedCompany) {
        Company existingCompany = getCompanyById(id);
        existingCompany.setName(updatedCompany.getName());
        existingCompany.setDescription(updatedCompany.getDescription());
        existingCompany.setWebsite(updatedCompany.getWebsite());
        return companyRepository.save(existingCompany);
    }

    public void deleteCompany(Long id) {
        Company company = getCompanyById(id);

        if (!jobRepository.findByCompanyId(id).isEmpty()) {
            throw new BusinessRuleException(
                    "Cannot delete this company because it has existing jobs. Delete its jobs first.");
        }

        companyRepository.delete(company);
    }
}