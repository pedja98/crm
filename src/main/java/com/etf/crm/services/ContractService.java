package com.etf.crm.services;

import com.etf.crm.entities.Contract;
import com.etf.crm.entities.Opportunity;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.repositories.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Contract createContract(Long companyId, Long opportunityId, Contract contract) {
        Opportunity opportunity = opportunityService.getOpportunityById(opportunityId);
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

    public Contract updateContract(Long id, Contract contract) {
        Contract existingContract = this.getContractById(id);
        existingContract.setName(contract.getName());
        existingContract.setReferenceNumber(contract.getReferenceNumber());
        existingContract.setStatus(contract.getStatus());
        existingContract.setArchiveStatus(contract.getArchiveStatus());
        existingContract.setComment(contract.getComment());
        existingContract.setModifiedBy(contract.getModifiedBy());
        return this.contractRepository.save(existingContract);
    }
}
