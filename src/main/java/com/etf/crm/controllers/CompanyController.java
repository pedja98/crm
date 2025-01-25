package com.etf.crm.controllers;

import com.etf.crm.dtos.CompanyDto;
import com.etf.crm.dtos.MessageResponse;
import com.etf.crm.entities.Company;
import com.etf.crm.enums.CompanyStatus;
import com.etf.crm.services.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @PostMapping
    public ResponseEntity<MessageResponse> createCompany(@RequestBody Company company) {
        return ResponseEntity.ok(new MessageResponse(companyService.saveCompany(company)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyDto> getCompanyById(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getCompanyById(id));
    }

    @GetMapping
    public ResponseEntity<List<CompanyDto>> getAllCompanies(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String hqAddress,
            @RequestParam(required = false) String contactPhone,
            @RequestParam(required = false) Integer numberOfEmployees,
            @RequestParam(required = false) Integer tin,
            @RequestParam(required = false) String bankName,
            @RequestParam(required = false) String bankAccountNumber,
            @RequestParam(required = false) CompanyStatus status,
            @RequestParam(required = false) String createdByUsername,
            @RequestParam(required = false) String modifiedByUsername) {

        List<CompanyDto> companies = companyService.getFilteredAndSortedCompanies(
                sortBy, sortOrder, name, hqAddress, contactPhone, numberOfEmployees, tin,
                bankName, bankAccountNumber, status, createdByUsername, modifiedByUsername
        );
        return ResponseEntity.ok(companies);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateCompany(@PathVariable Long id, @RequestBody CompanyDto companyDetails) {
        return ResponseEntity.ok(new MessageResponse(this.companyService.updateCompany(id, companyDetails)));
    }
}
