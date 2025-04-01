package com.etf.crm.controllers;

import com.etf.crm.dtos.OpportunityDto;
import com.etf.crm.enums.OpportunityStatus;
import com.etf.crm.enums.OpportunityType;
import com.etf.crm.services.OpportunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/opportunities")
public class OpportunityController {

    @Autowired
    private OpportunityService opportunityService;

    @GetMapping("/{id}")
    public ResponseEntity<OpportunityDto> getOpportunityById(@PathVariable Long id) {
        return ResponseEntity.ok(opportunityService.getOpportunityById(id));
    }

    @GetMapping
    public ResponseEntity<List<OpportunityDto>> getAllOpportunities(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) String name,
            @RequestParam(required = false, value = "type") List<OpportunityType> types,
            @RequestParam(required = false, value = "status") List<OpportunityStatus> statuses) {
        return ResponseEntity.ok(opportunityService.getAllOpportunities(sortBy, sortOrder, name, types, statuses));
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<String> closeOpportunity(@PathVariable Long id) {
        return ResponseEntity.ok(opportunityService.closeOpportunity(id));
    }
}