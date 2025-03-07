package com.etf.crm.services;

import com.etf.crm.dtos.CustomerSessionDto;
import com.etf.crm.dtos.SaveCustomerSessionDto;
import com.etf.crm.entities.CustomerSession;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.repositories.CustomerSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.etf.crm.common.CrmConstants.ErrorCodes.CUSTOMER_SESSION_NOT_FOUND;
import static com.etf.crm.common.CrmConstants.SuccessCodes.*;

@Service
public class CustomerSessionService {

    @Autowired
    private CustomerSessionRepository customerSessionRepository;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private OpportunityService opportunityService;

    @Transactional
    public String createCustomerSession(SaveCustomerSessionDto customerSession) {
        return CUSTOMER_SESSION_CREATED;
    }

    public CustomerSessionDto getCustomerSessionById(Long id) {
        return this.customerSessionRepository.findCustomerSessionDtoByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(CUSTOMER_SESSION_NOT_FOUND));
    }

    public List<CustomerSession> getAllCustomerSessions() {
        return this.customerSessionRepository.findAllByDeletedFalse();
    }

    @Transactional
    public String updateCustomerSession(Long id, CustomerSession customerSession) {
        return CUSTOMER_SESSION_UPDATED;
    }
}
