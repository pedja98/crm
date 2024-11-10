package com.etf.crm.controllers;

import com.etf.crm.entities.CustomerSession;
import com.etf.crm.services.CustomerSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customer-sessions")
public class CustomerSessionController {

    @Autowired
    private CustomerSessionService customerSessionService;

    @PostMapping("/company/{companyId}/opportunity/{opportunityId}")
    public ResponseEntity<CustomerSession> createCustomerSession(
            @PathVariable Long companyId,
            @PathVariable Long opportunityId,
            @RequestBody CustomerSession customerSession) {
        return ResponseEntity.ok(customerSessionService.saveCustomerSession(companyId, opportunityId, customerSession));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerSession> getCustomerSessionById(@PathVariable Long id) {
        return ResponseEntity.ok(customerSessionService.getCustomerSessionById(id));
    }

    @GetMapping
    public ResponseEntity<List<CustomerSession>> getAllCustomerSessions() {
        return ResponseEntity.ok(customerSessionService.getAllCustomerSessions());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerSession> updateCustomerSession(
            @PathVariable Long id,
            @RequestBody CustomerSession customerSessionDetails) {
        return ResponseEntity.ok(customerSessionService.updateCustomerSession(id, customerSessionDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomerSession(@PathVariable Long id) {
        customerSessionService.deleteCustomerSession(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CustomerSession> partialUpdateCustomerSession(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        updates.forEach((fieldName, fieldValue) -> {
            this.customerSessionService.partialUpdateCustomerSession(id, fieldName, fieldValue);
        });
        return ResponseEntity.ok(this.customerSessionService.getCustomerSessionById(id));
    }
}
