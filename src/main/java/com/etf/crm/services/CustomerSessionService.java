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
        Company company = companyService.getCompanyById(companyId);
        Opportunity opportunity = opportunityService.getOpportunityById(opportunityId);
        customerSession.setCompany(company);
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

    public CustomerSession updateCustomerSession(Long id, CustomerSession customerSessionDetails) {
        CustomerSession existingCustomerSession = this.getCustomerSessionById(id);
        existingCustomerSession.setName(customerSessionDetails.getName());
        existingCustomerSession.setDescription(customerSessionDetails.getDescription());
        existingCustomerSession.setStatus(customerSessionDetails.getStatus());
        existingCustomerSession.setType(customerSessionDetails.getType());
        existingCustomerSession.setMode(customerSessionDetails.getMode());
        existingCustomerSession.setSessionStart(customerSessionDetails.getSessionStart());
        existingCustomerSession.setSessionEnd(customerSessionDetails.getSessionEnd());
        existingCustomerSession.setModifiedBy(customerSessionDetails.getModifiedBy());
        return this.customerSessionRepository.save(existingCustomerSession);
    }

    public void partialUpdateCustomerSession(Long id, String fieldName, Object fieldValue) {
        CustomerSession existingCustomerSession = this.getCustomerSessionById(id);
        try {
            Field field = CustomerSession.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(existingCustomerSession, fieldValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException("Invalid field name: " + fieldName, e);
        }
        this.customerSessionRepository.save(existingCustomerSession);
    }

}
