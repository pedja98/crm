package com.etf.crm.services;

import com.etf.crm.entities.Company;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static com.etf.crm.common.CrmConstants.ErrorCodes.COMPANY_NOT_FOUND;

@Service
public class CompanyService {
    @Autowired
    private CompanyRepository companyRepository;

    public Company saveCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public Company getCompanyById(Long id) {
        return this.companyRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(COMPANY_NOT_FOUND));
    }

    public List<Company> getAllCompanies() {
        return this.companyRepository.findAllByDeletedFalse();
    }

    public void deleteCompany(Long id) {
        Optional<Company> existingCompanyOpt = this.companyRepository.findByIdAndDeletedFalse(id);
        if (existingCompanyOpt.isPresent()) {
            Company existingCompany = existingCompanyOpt.get();
            existingCompany.setDeleted(true);
            this.companyRepository.save(existingCompany);
        }
    }

    public Company updateCompany(Long id, Company companyDetails) {
        Company existingCompany = this.getCompanyById(id);
        existingCompany.setName(companyDetails.getName());
        existingCompany.setHqAddress(companyDetails.getHqAddress());
        existingCompany.setIndustry(companyDetails.getIndustry());
        existingCompany.setContactPhone(companyDetails.getContactPhone());
        existingCompany.setNumberOfEmployees(companyDetails.getNumberOfEmployees());
        existingCompany.setTin(companyDetails.getTin());
        existingCompany.setBankName(companyDetails.getBankName());
        existingCompany.setBankAccountNumber(companyDetails.getBankAccountNumber());
        existingCompany.setComment(companyDetails.getComment());
        existingCompany.setModifiedBy(companyDetails.getModifiedBy());
        return this.companyRepository.save(existingCompany);
    }

    public void partialUpdateCompany(Long id, String fieldName, Object fieldValue) {
        Company existingCompany = this.getCompanyById(id);
        try {
            Field field = Company.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(existingCompany, fieldValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException("Invalid field name: " + fieldName, e);
        }
        this.companyRepository.save(existingCompany);
    }
}
