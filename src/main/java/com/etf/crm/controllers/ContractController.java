package com.etf.crm.controllers;

import com.etf.crm.entities.Contract;
import com.etf.crm.services.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contracts")
public class ContractController {

    @Autowired
    private ContractService contractService;

    @PostMapping("/{companyId}/{opportunityId}")
    public ResponseEntity<Contract> createContract(@PathVariable Long companyId, @PathVariable Long opportunityId, @RequestBody Contract contract) {
        Contract savedContract = contractService.saveContract(companyId, opportunityId, contract);
        return ResponseEntity.ok(savedContract);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contract> getContractById(@PathVariable Long id) {
        Contract contract = contractService.getContractById(id);
        return ResponseEntity.ok(contract);
    }

    @GetMapping
    public ResponseEntity<List<Contract>> getAllContracts() {
        List<Contract> contracts = contractService.getAllContracts();
        return ResponseEntity.ok(contracts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contract> updateContract(@PathVariable Long id, @RequestBody Contract contractDetails) {
        Contract updatedContract = contractService.updateContract(id, contractDetails);
        return ResponseEntity.ok(updatedContract);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContract(@PathVariable Long id) {
        contractService.deleteContract(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Contract> partialUpdateContract(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        updates.forEach((fieldName, fieldValue) -> {
            this.contractService.partialUpdateContract(id, fieldName, fieldValue);
        });
        Contract updatedContract = this.contractService.getContractById(id);
        return ResponseEntity.ok(updatedContract);
    }
}
