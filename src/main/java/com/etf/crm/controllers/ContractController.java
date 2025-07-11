package com.etf.crm.controllers;

import com.etf.crm.dtos.*;
import com.etf.crm.enums.ContractStatus;
import com.etf.crm.enums.OpportunityType;
import com.etf.crm.services.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/contracts")
public class ContractController {

    @Autowired
    private ContractService contractService;

    @PostMapping
    public ResponseEntity<MessageResponse> createContract(@RequestBody CreateContractDto contract) {
        return ResponseEntity.ok(new MessageResponse(this.contractService.createContract(contract)));
    }

    @GetMapping
    public ResponseEntity<List<ContractDto>> getAllContracts(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String referenceNumber,
            @RequestParam(required = false, value = "status") List<ContractStatus> statuses,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) Long opportunityId) {
        return ResponseEntity.ok(contractService.getAllContracts(sortBy, sortOrder, name, referenceNumber, statuses, companyId, opportunityId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContractDto> getOfferById(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.getContractById(id));
    }

    @PutMapping("/{id}/signed")
    public ResponseEntity<MessageResponse> signedContract(@PathVariable Long id, @RequestBody ContractSignDto body) {
        return ResponseEntity.ok(new MessageResponse(this.contractService.signContract(id, body)));
    }

    @PatchMapping("/{id}/verify")
    public ResponseEntity<MessageResponse> verifyContract(@PathVariable Long id) {
        return ResponseEntity.ok(new MessageResponse(this.contractService.verifyContract(id)));
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<MessageResponse> closeContract(@PathVariable Long id) {
        return ResponseEntity.ok(new MessageResponse(this.contractService.closeContract(id)));
    }

    @GetMapping("/report")
    public ResponseEntity<List<ContractReportDto>> getContractReport(
            @RequestParam(required = false) List<Long> regions,
            @RequestParam(required = false) List<Long> shops,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime signatureStartDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime signatureEndDate,
            @RequestParam(required = false) List<OpportunityType> opportunityTypes,
            @RequestParam(required = false) List<ContractStatus> contractStatuses) {
        return ResponseEntity.ok(this.contractService.getContractReport(
                regions, shops, signatureStartDate, signatureEndDate, opportunityTypes, contractStatuses));
    }
}
