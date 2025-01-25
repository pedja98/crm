package com.etf.crm.services;

import com.etf.crm.entities.CustomerSession;
import com.etf.crm.entities.Company;
import com.etf.crm.entities.Opportunity;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.repositories.CustomerSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;

import static com.etf.crm.common.CrmConstants.ErrorCodes.CUSTOMER_SESSION_NOT_FOUND;

@Service
public class CustomerSessionService {

    @Autowired
    private CustomerSessionRepository customerSessionRepository;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private OpportunityService opportunityService;

    public CustomerSession saveCustomerSession(Long companyId, Long opportunityId, CustomerSession customerSession) {
        Opportunity opportunity = opportunityService.getOpportunityById(opportunityId);
        customerSession.setOpportunity(opportunity);
        return this.customerSessionRepository.save(customerSession);
    }

    public CustomerSession getCustomerSessionById(Long id) {
        return this.customerSessionRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(CUSTOMER_SESSION_NOT_FOUND));
    }

    public List<CustomerSession> getAllCustomerSessions() {
        return this.customerSessionRepository.findAllByDeletedFalse();
    }

    public void deleteCustomerSession(Long id) {
        CustomerSession customerSession = this.getCustomerSessionById(id);
        customerSession.setDeleted(true);
        this.customerSessionRepository.save(customerSession);
    }

    public CustomerSession updateCustomerSession(Long id, CustomerSession customerSession) {
        CustomerSession existingCustomerSession = this.getCustomerSessionById(id);
        existingCustomerSession.setName(customerSession.getName());
        existingCustomerSession.setDescription(customerSession.getDescription());
        existingCustomerSession.setStatus(customerSession.getStatus());
        existingCustomerSession.setType(customerSession.getType());
        existingCustomerSession.setMode(customerSession.getMode());
        existingCustomerSession.setSessionStart(customerSession.getSessionStart());
        existingCustomerSession.setSessionEnd(customerSession.getSessionEnd());
        existingCustomerSession.setModifiedBy(customerSession.getModifiedBy());
        return this.customerSessionRepository.save(existingCustomerSession);
    }
}
