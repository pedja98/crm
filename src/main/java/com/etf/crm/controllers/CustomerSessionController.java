package com.etf.crm.controllers;

import com.etf.crm.dtos.CustomerSessionDto;
import com.etf.crm.dtos.MessageResponse;
import com.etf.crm.dtos.SaveCustomerSessionDto;
import com.etf.crm.enums.*;
import com.etf.crm.services.CustomerSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer-sessions")
public class CustomerSessionController {

    @Autowired
    private CustomerSessionService customerSessionService;

    @PostMapping
    public ResponseEntity<MessageResponse> createCustomerSession(@RequestBody SaveCustomerSessionDto customerSession) {
        return ResponseEntity.ok(new MessageResponse(customerSessionService.createCustomerSession(customerSession)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerSessionDto> getCustomerSessionById(@PathVariable Long id) {
        return ResponseEntity.ok(customerSessionService.getCustomerSessionById(id));
    }

    @GetMapping
    public ResponseEntity<List<CustomerSessionDto>> getAllCustomerSessions(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) String name,
            @RequestParam(required = false, value = "type") List<CustomerSessionType> types,
            @RequestParam(required = false, value = "mode") List<CustomerSessionMode> modes,
            @RequestParam(required = false, value = "outcome") List<CustomerSessionOutcome> outcomes,
            @RequestParam(required = false, value = "status") List<CustomerSessionStatus> statuses,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) Long opportunityId
    ) {
        return ResponseEntity.ok(customerSessionService.getAllCustomerSessions(sortBy, sortOrder, name, types, modes, outcomes, statuses, companyId, opportunityId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateCustomerSession(
            @PathVariable Long id,
            @RequestBody SaveCustomerSessionDto customerSessionDetails) {
        return ResponseEntity.ok(new MessageResponse(customerSessionService.updateCustomerSession(id, customerSessionDetails)));
    }
}
