package com.etf.crm.controllers;

import com.etf.crm.entities.Opportunity;
import com.etf.crm.services.OpportunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/opportunities")
public class OpportunityController {

    @Autowired
    private OpportunityService opportunityService;

    @PostMapping("/{companyId}")
    public ResponseEntity<Opportunity> createOpportunity(@PathVariable Long companyId, @RequestBody Opportunity opportunity) {
        Opportunity savedOpportunity = opportunityService.saveOpportunity(companyId, opportunity);
        return ResponseEntity.ok(savedOpportunity);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Opportunity> getOpportunityById(@PathVariable Long id) {
        Opportunity opportunity = opportunityService.getOpportunityById(id);
        return ResponseEntity.ok(opportunity);
    }

    @GetMapping
    public ResponseEntity<List<Opportunity>> getAllOpportunities() {
        List<Opportunity> opportunities = opportunityService.getAllOpportunities();
        return ResponseEntity.ok(opportunities);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Opportunity> updateOpportunity(@PathVariable Long id, @RequestBody Opportunity opportunityDetails) {
        Opportunity updatedOpportunity = opportunityService.updateOpportunity(id, opportunityDetails);
        return ResponseEntity.ok(updatedOpportunity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOpportunity(@PathVariable Long id) {
        opportunityService.deleteOpportunity(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Opportunity> partialUpdate_(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        updates.forEach((fieldName, fieldValue) -> {
            this.opportunityService.partialUpdateOpportunity(id, fieldName, fieldValue);
        });
        Opportunity updatedOpportunity = this.opportunityService.getOpportunityById(id);
        return ResponseEntity.ok(updatedOpportunity);
    }
}