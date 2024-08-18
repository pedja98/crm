package com.etf.crm.services;

import com.etf.crm.entities.Contract;
import com.etf.crm.entities.Company;
import com.etf.crm.entities.Opportunity;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.repositories.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;

import static com.etf.crm.common.CrmConstants.ErrorCodes.CONTRACT_NOT_FOUND;

@Service
public class ContractService {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private OpportunityService opportunityService;

    public Contract saveContract(Long companyId, Long opportunityId, Contract contract) {
        Company company = companyService.getCompanyById(companyId);
        Opportunity opportunity = opportunityService.getOpportunityById(opportunityId);
        contract.setCompany(company);
        contract.setOpportunity(opportunity);
        return this.contractRepository.save(contract);
    }

    public Contract getContractById(Long id) {
        return this.contractRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(CONTRACT_NOT_FOUND));
    }

    public List<Contract> getAllContracts() {
        return this.contractRepository.findAllByDeletedFalse();
    }

    public void deleteContract(Long id) {
        Contract contract = this.getContractById(id);
        contract.setDeleted(true);
        this.contractRepository.save(contract);
    }

    public Contract updateContract(Long id, Contract contractDetails) {
        Contract existingContract = this.getContractById(id);
        existingContract.setName(contractDetails.getName());
        existingContract.setReferenceNumber(contractDetails.getReferenceNumber());
        existingContract.setStatus(contractDetails.getStatus());
        existingContract.setArchiveStatus(contractDetails.getArchiveStatus());
        existingContract.setComment(contractDetails.getComment());
        existingContract.setModifiedBy(contractDetails.getModifiedBy());
        return this.contractRepository.save(existingContract);
    }

    public void partialUpdateContract(Long id, String fieldName, Object fieldValue) {
        Contract existingContract = this.getContractById(id);
        try {
            Field field = Contract.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(existingContract, fieldValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException("Invalid field name: " + fieldName, e);
        }
        this.contractRepository.save(existingContract);
    }
}
