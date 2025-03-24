package com.etf.crm.services;

import com.etf.crm.dtos.CustomerSessionDto;
import com.etf.crm.dtos.SaveCustomerSessionDto;
import com.etf.crm.entities.Company;
import com.etf.crm.entities.CustomerSession;
import com.etf.crm.entities.Opportunity;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.filters.SetCurrentUserFilter;
import com.etf.crm.repositories.CompanyRepository;
import com.etf.crm.repositories.CustomerSessionRepository;
import com.etf.crm.repositories.OpportunityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.etf.crm.common.CrmConstants.ErrorCodes.*;
import static com.etf.crm.common.CrmConstants.SuccessCodes.*;

@Service
public class CustomerSessionService {

    @Autowired
    private CustomerSessionRepository customerSessionRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Transactional
    public String createCustomerSession(SaveCustomerSessionDto customerSessionDetails) {
        Company company = companyRepository.findByIdAndDeletedFalse(customerSessionDetails.getCompany())
                .orElseThrow(() -> new ItemNotFoundException(COMPANY_NOT_FOUND));

        Opportunity opportunity = customerSessionDetails.getOpportunity() != null
                ? opportunityRepository.findByIdAndDeletedFalse(customerSessionDetails.getOpportunity())
                    .orElseThrow(() -> new ItemNotFoundException(OPPORTUNITY_NOT_FOUND))
                : null;

        CustomerSession customerSession = CustomerSession.builder()
                .name(customerSessionDetails.getName())
                .description(customerSessionDetails.getDescription())
                .status(customerSessionDetails.getStatus())
                .type(customerSessionDetails.getType())
                .mode(customerSessionDetails.getMode())
                .outcome(customerSessionDetails.getOutcome())
                .sessionStart(customerSessionDetails.getSessionStart())
                .sessionEnd(customerSessionDetails.getSessionEnd())
                .company(company)
                .opportunity(opportunity)
                .createdBy(SetCurrentUserFilter.getCurrentUser())
                .deleted(false)
                .build();

        this.customerSessionRepository.save(customerSession);
        return CUSTOMER_SESSION_CREATED;
    }

    public CustomerSessionDto getCustomerSessionById(Long id) {
        return this.customerSessionRepository.findCustomerSessionDtoByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(CUSTOMER_SESSION_NOT_FOUND));
    }

    public List<CustomerSessionDto> getAllCustomerSessions() {
        return this.customerSessionRepository.findAllCustomerSessionDtoByDeletedFalse().orElseThrow(() -> new ItemNotFoundException(COMPANY_NOT_FOUND));
    }

    @Transactional
    public String updateCustomerSession(Long id, SaveCustomerSessionDto customerSessionDetails) {
        CustomerSession customerSession = this.customerSessionRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(CUSTOMER_SESSION_NOT_FOUND));

        Company company = companyRepository.findByIdAndDeletedFalse(customerSessionDetails.getCompany())
                .orElseThrow(() -> new ItemNotFoundException(COMPANY_NOT_FOUND));

        Opportunity opportunity = customerSessionDetails.getOpportunity() != null
                ? opportunityRepository.findByIdAndDeletedFalse(customerSessionDetails.getOpportunity())
                .orElseThrow(() -> new ItemNotFoundException(OPPORTUNITY_NOT_FOUND))
                : null;

        customerSession.setName(customerSessionDetails.getName());
        customerSession.setDescription(customerSessionDetails.getDescription());
        customerSession.setStatus(customerSessionDetails.getStatus());
        customerSession.setType(customerSessionDetails.getType());
        customerSession.setMode(customerSessionDetails.getMode());
        customerSession.setOutcome(customerSessionDetails.getOutcome());
        customerSession.setSessionStart(customerSessionDetails.getSessionStart());
        customerSession.setSessionEnd(customerSessionDetails.getSessionEnd());
        customerSession.setCompany(company);
        customerSession.setOpportunity(opportunity);
        customerSession.setModifiedBy(SetCurrentUserFilter.getCurrentUser());

        this.customerSessionRepository.save(customerSession);
        return CUSTOMER_SESSION_UPDATED;
    }
}
