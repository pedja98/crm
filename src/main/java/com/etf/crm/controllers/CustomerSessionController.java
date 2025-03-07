package com.etf.crm.controllers;

import com.etf.crm.dtos.CustomerSessionDto;
import com.etf.crm.dtos.SaveCustomerSessionDto;
import com.etf.crm.entities.CustomerSession;
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
    public ResponseEntity<String> createCustomerSession(@RequestBody SaveCustomerSessionDto customerSession) {
        return ResponseEntity.ok(customerSessionService.saveCustomerSession(customerSession));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerSessionDto> getCustomerSessionById(@PathVariable Long id) {
        return ResponseEntity.ok(customerSessionService.getCustomerSessionById(id));
    }

    @GetMapping
    public ResponseEntity<List<CustomerSession>> getAllCustomerSessions() {
        return ResponseEntity.ok(customerSessionService.getAllCustomerSessions());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCustomerSession(
            @PathVariable Long id,
            @RequestBody CustomerSession customerSessionDetails) {
        return ResponseEntity.ok(customerSessionService.updateCustomerSession(id, customerSessionDetails));
    }
}
