package com.etf.crm.services;

import com.etf.crm.entities.Opportunity;
import com.etf.crm.entities.Company;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.repositories.OpportunityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;

import static com.etf.crm.common.CrmConstants.ErrorCodes.OPPORTUNITY_NOT_FOUND;

@Service
public class OpportunityService {

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private CompanyService companyService;

    public Opportunity saveOpportunity(Long companyId, Opportunity opportunity) {
        return this.opportunityRepository.save(opportunity);
    }

    public Opportunity getOpportunityById(Long id) {
        return this.opportunityRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(OPPORTUNITY_NOT_FOUND));
    }

    public List<Opportunity> getAllOpportunities() {
        return this.opportunityRepository.findAllByDeletedFalse();
    }

    public void deleteOpportunity(Long id) {
        Opportunity opportunity = this.getOpportunityById(id);
        opportunity.setDeleted(true);
        this.opportunityRepository.save(opportunity);
    }

    public Opportunity updateOpportunity(Long id, Opportunity opportunity) {
        Opportunity existingOpportunity = this.getOpportunityById(id);
        existingOpportunity.setName(opportunity.getName());
        existingOpportunity.setModifiedBy(opportunity.getModifiedBy());
        return this.opportunityRepository.save(existingOpportunity);
    }
}
