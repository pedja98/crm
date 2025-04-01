package com.etf.crm.services;

import com.etf.crm.dtos.CreateOpportunityDto;
import com.etf.crm.entities.Company;
import com.etf.crm.entities.Opportunity;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.filters.SetCurrentUserFilter;
import com.etf.crm.repositories.CompanyRepository;
import com.etf.crm.repositories.OpportunityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.etf.crm.common.CrmConstants.SuccessCodes.*;
import static com.etf.crm.common.CrmConstants.ErrorCodes.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.etf.crm.common.CrmConstants.ErrorCodes.OPPORTUNITY_NOT_FOUND;

@Service
public class OpportunityService {

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Transactional
    public String createOpportunity(CreateOpportunityDto opportunityDetails) {
        Company company = companyRepository.findByIdAndDeletedFalse(opportunityDetails.getCompanyId())
                .orElseThrow(() -> new ItemNotFoundException(COMPANY_NOT_FOUND));

        Opportunity opportunity = Opportunity.builder()
                .company(company)
                .name("OPP " + company.getName() + " " + (new SimpleDateFormat("dd-MM-yyyy")).format(new Date()))
                .type(opportunityDetails.getType())
                .createdBy(SetCurrentUserFilter.getCurrentUser())
                .deleted(false)
                .build();

        this.opportunityRepository.save(opportunity);
        return OPPORTUNITY_CREATED;
    }

    public Opportunity getOpportunityById(Long id) {
        return this.opportunityRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(OPPORTUNITY_NOT_FOUND));
    }

    public List<Opportunity> getAllOpportunities() {
        return this.opportunityRepository.findAllByDeletedFalse();
    }

    @Transactional
    public Opportunity updateOpportunity(Long id, Opportunity opportunity) {
        Opportunity existingOpportunity = this.getOpportunityById(id);
        existingOpportunity.setName(opportunity.getName());
        existingOpportunity.setModifiedBy(opportunity.getModifiedBy());
        return this.opportunityRepository.save(existingOpportunity);
    }
}
