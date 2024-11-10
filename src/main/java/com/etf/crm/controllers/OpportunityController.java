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
        return ResponseEntity.ok(opportunityService.saveOpportunity(companyId, opportunity));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Opportunity> getOpportunityById(@PathVariable Long id) {
        return ResponseEntity.ok(opportunityService.getOpportunityById(id));
    }

    @GetMapping
    public ResponseEntity<List<Opportunity>> getAllOpportunities() {
        return ResponseEntity.ok(opportunityService.getAllOpportunities());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Opportunity> updateOpportunity(@PathVariable Long id, @RequestBody Opportunity opportunityDetails) {
        return ResponseEntity.ok(opportunityService.updateOpportunity(id, opportunityDetails));
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
        return ResponseEntity.ok(this.opportunityService.getOpportunityById(id));
    }
}