package com.etf.crm.services;

import com.etf.crm.dtos.CompanyDto;
import com.etf.crm.dtos.SaveCompanyDto;
import com.etf.crm.entities.Company;
import com.etf.crm.entities.User;
import com.etf.crm.enums.CompanyStatus;
import com.etf.crm.exceptions.*;
import com.etf.crm.filters.SetCurrentUserFilter;
import com.etf.crm.repositories.CompanyRepository;
import com.etf.crm.repositories.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public String createCompany(SaveCompanyDto companyDto) {
        if (companyRepository.findByTinAndDeletedFalse(companyDto.getTin()).isPresent()) {
            throw new DuplicateItemException(TIN_ALREADY_TAKEN);
        }

        if (Objects.equals(companyDto.getAssignedTo(), companyDto.getTemporaryAssignedTo())) {
            throw new BadRequestException(ASSIGNED_TO_SAME_AS_TEMPORARY);
        }

        User assignedTo = userRepository.findById(companyDto.getAssignedTo())
                .orElseThrow(() -> new ItemNotFoundException(USER_NOT_FOUND));

        User temporaryAssignedTo = companyDto.getTemporaryAssignedTo() != null
                ? userRepository.findById(companyDto.getTemporaryAssignedTo())
                .orElseThrow(() -> new ItemNotFoundException(USER_NOT_FOUND))
                : null;

        Company company = Company.builder()
                .name(companyDto.getName())
                .hqAddress(companyDto.getHqAddress())
                .contactPhone(companyDto.getContactPhone())
                .numberOfEmployees(companyDto.getNumberOfEmployees())
                .tin(companyDto.getTin())
                .bankName(companyDto.getBankName())
                .bankAccountNumber(companyDto.getBankAccountNumber())
                .industry(companyDto.getIndustry())
                .status(CompanyStatus.POTENTIAL)
                .comment(companyDto.getComment())
                .assignedTo(assignedTo)
                .temporaryAssignedTo(temporaryAssignedTo)
                .createdBy(SetCurrentUserFilter.getCurrentUser())
                .deleted(false)
                .build();

        companyRepository.save(company);
        return COMPANY_CREATED;
    }

    public CompanyDto getCompanyById(Long id) {
        return this.companyRepository.findCompanyDtoByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(COMPANY_NOT_FOUND));
    }

    public List<CompanyDto> getFilteredAndSortedCompanies(
            String sortBy, String sortOrder,
            String name, String hqAddress, String contactPhone, Integer numberOfEmployees, Integer tin,
            String bankName, String bankAccountNumber, List<CompanyStatus> statuses, String createdByUsername, String modifiedByUsername) {

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
        filters.put("status", statuses);
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
                            } else if (value instanceof List<?> listValue) {
                                return fieldValue != null && listValue.contains(fieldValue);
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
    public String updateCompany(Long id, SaveCompanyDto companyDetails) {
        Company company = this.companyRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(COMPANY_NOT_FOUND));

        for (Field field : SaveCompanyDto.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object newValue = field.get(companyDetails);
                if (field.getName().equals("status") && newValue == null) {
                    continue;
                }
                if (field.getName().equals("assignedTo")) {
                    newValue = userRepository.findById((Long) field.get(companyDetails))
                            .orElseThrow(() -> new ItemNotFoundException(USER_NOT_FOUND));
                } else if (field.getName().equals("temporaryAssignedTo")) {
                    newValue = newValue == null
                            ? null
                            : userRepository.findById((Long) field.get(companyDetails))
                            .orElseThrow(() -> new ItemNotFoundException(USER_NOT_FOUND));
                }
                Field companyField = Company.class.getDeclaredField(field.getName());
                companyField.setAccessible(true);
                companyField.set(company, newValue);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new PropertyCopyException(ENTITY_UPDATE_ERROR);
            }
        }

        company.setModifiedBy(SetCurrentUserFilter.getCurrentUser());
        this.companyRepository.save(company);

        return COMPANY_UPDATED;
    }
}
