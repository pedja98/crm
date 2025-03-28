package com.etf.crm.controllers;

import com.etf.crm.dtos.CompanyContactRelationDto;
import com.etf.crm.dtos.CreateCompanyContactRelationDto;
import com.etf.crm.dtos.MessageResponse;
import com.etf.crm.entities.CompanyContactRelation;
import com.etf.crm.services.CompanyContactRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/company-contact-relations")
public class CompanyContactRelationController {

    @Autowired
    private CompanyContactRelationService relationService;

    @PostMapping
    public ResponseEntity<MessageResponse> createRelation(@RequestBody CreateCompanyContactRelationDto relation) {
        return ResponseEntity.ok(new MessageResponse(relationService.createRelation(relation)));
    }

    @GetMapping
    public ResponseEntity<List<CompanyContactRelationDto>> getAllRelationByContactId(@RequestParam(required = true) Long contactId) {
        return ResponseEntity.ok(relationService.getAllRelationByContactId(contactId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateRelation(@PathVariable Long id, @RequestBody CompanyContactRelation relationDetails) {
        return ResponseEntity.ok(new MessageResponse(relationService.updateRelation(id, relationDetails)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteRelation(@PathVariable Long id) {
        return ResponseEntity.ok(new MessageResponse(this.relationService.deleteRelation(id)));
    }
}
