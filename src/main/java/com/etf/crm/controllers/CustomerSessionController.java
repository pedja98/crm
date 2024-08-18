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
        CustomerSession savedCustomerSession = customerSessionService.saveCustomerSession(companyId, opportunityId, customerSession);
        return ResponseEntity.ok(savedCustomerSession);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerSession> getCustomerSessionById(@PathVariable Long id) {
        CustomerSession customerSession = customerSessionService.getCustomerSessionById(id);
        return ResponseEntity.ok(customerSession);
    }

    @GetMapping
    public ResponseEntity<List<CustomerSession>> getAllCustomerSessions() {
        List<CustomerSession> customerSessions = customerSessionService.getAllCustomerSessions();
        return ResponseEntity.ok(customerSessions);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerSession> updateCustomerSession(
            @PathVariable Long id,
            @RequestBody CustomerSession customerSessionDetails) {
        CustomerSession updatedCustomerSession = customerSessionService.updateCustomerSession(id, customerSessionDetails);
        return ResponseEntity.ok(updatedCustomerSession);
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
        CustomerSession updatedCustomerSession = this.customerSessionService.getCustomerSessionById(id);
        return ResponseEntity.ok(updatedCustomerSession);
    }
}
