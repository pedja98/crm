package com.etf.crm.services;

import com.etf.crm.dtos.CompanyDto;
import com.etf.crm.entities.Company;
import com.etf.crm.entities.User;
import com.etf.crm.enums.CompanyStatus;
import com.etf.crm.exceptions.InvalidAttributeValueException;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.exceptions.PropertyCopyException;
import com.etf.crm.filters.SetCurrentUserFilter;
import com.etf.crm.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;

import static com.etf.crm.common.CrmConstants.ErrorCodes.*;
import static com.etf.crm.common.CrmConstants.SuccessCodes.*;

@Service
public class CompanyService {
    @Autowired
    private CompanyRepository companyRepository;

    @Transactional
    public String saveCompany(Company company) {
        company.setCreatedBy(SetCurrentUserFilter.getCurrentUser());
        User currentUser = SetCurrentUserFilter.getCurrentUser();
        company.setCreatedBy(currentUser);
        this.companyRepository.save(company);
        return COMPANY_CREATED;
    }

    public CompanyDto getCompanyById(Long id) {
        return this.companyRepository.findCompanyDtoByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(COMPANY_NOT_FOUND));
    }

    public List<CompanyDto> getFilteredAndSortedCompanies(
            String sortBy, String sortOrder,
            String name, String hqAddress, String contactPhone, Integer numberOfEmployees, Integer tin,
            String bankName, String bankAccountNumber, CompanyStatus status, String createdByUsername, String modifiedByUsername) {

        List<CompanyDto> companies = companyRepository.findAllCompanyDtoByDeletedFalse()
                .orElseThrow(() -> new ItemNotFoundException(COMPANY_NOT_FOUND));

        Map<String, Object> filters = new HashMap<>();
        filters.put("name", name);
        filters.put("hqAddress", hqAddress);
        filters.put("contactPhone", contactPhone);
        filters.put("numberOfEmployees", numberOfEmployees);
        filters.put("tin", tin);
        filters.put("bankName", bankName);
        filters.put("bankAccountNumber", bankAccountNumber);
        filters.put("status", status);
        filters.put("createdByUsername", createdByUsername);
        filters.put("modifiedByUsername", modifiedByUsername);

        List<Predicate<CompanyDto>> predicates = filters.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .map(entry -> {
                    String fieldName = entry.getKey();
                    Object value = entry.getValue();

                    return (Predicate<CompanyDto>) company -> {
                        try {
                            Field field = CompanyDto.class.getDeclaredField(fieldName);
                            field.setAccessible(true);
                            Object fieldValue = field.get(company);

                            if (value instanceof String stringValue) {
                                return fieldValue != null && fieldValue.toString().toLowerCase().contains(stringValue.toLowerCase());
                            } else if (value instanceof Integer intValue) {
                                return fieldValue != null && fieldValue.equals(intValue);
                            } else if (value instanceof CompanyStatus statusValue) {
                                return fieldValue != null && fieldValue.equals(statusValue);
                            }
                            return false;
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            throw new RuntimeException(ILLEGAL_SORT_PARAMETER + ": " + fieldName, e);
                        }
                    };
                })
                .toList();

        for (Predicate<CompanyDto> predicate : predicates) {
            companies = companies.stream().filter(predicate).toList();
        }

        if (sortBy != null) {
            Comparator<CompanyDto> comparator = Comparator.comparing(company -> {
                try {
                    Field field = CompanyDto.class.getDeclaredField(sortBy);
                    field.setAccessible(true);
                    return (Comparable) field.get(company);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new IllegalArgumentException(ILLEGAL_SORT_PARAMETER + ": " + sortBy, e);
                }
            });

            if ("desc".equalsIgnoreCase(sortOrder)) {
                comparator = comparator.reversed();
            }
            companies = companies.stream().sorted(comparator).toList();
        }

        return companies;
    }

    @Transactional
    public String updateCompany(Long id, CompanyDto companyDetails) {
        Company company = this.companyRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(COMPANY_NOT_FOUND));

        for (Field field : CompanyDto.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object newValue = field.get(companyDetails);

                if (String.valueOf(newValue != null ? newValue : "").isEmpty()) {
                    throw new InvalidAttributeValueException(CAN_NOT_INSERT_EMPTY_VALUE);
                }

                Field companyField = Company.class.getDeclaredField(field.getName());
                companyField.setAccessible(true);
                companyField.set(company, newValue);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new PropertyCopyException(ENTITY_UPDATE_ERROR);
            }
        }

        User currentUser = SetCurrentUserFilter.getCurrentUser();
        company.setModifiedBy(currentUser);
        this.companyRepository.save(company);

        return COMPANY_UPDATED;
    }

}
