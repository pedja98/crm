package com.etf.crm.controllers;

import com.etf.crm.entities.CompanyContactRelation;
import com.etf.crm.enums.CompanyContactRelationType;
import com.etf.crm.services.CompanyContactRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/company-contact-relations")
public class CompanyContactRelationController {

    @Autowired
    private CompanyContactRelationService relationService;

    @PostMapping
    public ResponseEntity<CompanyContactRelation> createRelation(
            @RequestParam Long contactId,
            @RequestParam Long companyId,
            @RequestParam CompanyContactRelationType relationType,
            @RequestParam String roleDescription) {
        return ResponseEntity.ok(relationService.saveRelation(contactId, companyId, relationType, roleDescription));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyContactRelation> getRelationById(@PathVariable Long id) {
        return ResponseEntity.ok(relationService.getRelationById(id));
    }

    @GetMapping
    public ResponseEntity<List<CompanyContactRelation>> getAllRelations() {
        return ResponseEntity.ok(relationService.getAllRelations());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyContactRelation> updateRelation(@PathVariable Long id, @RequestBody CompanyContactRelation relationDetails) {
        return ResponseEntity.ok(relationService.updateRelation(id, relationDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRelation(@PathVariable Long id) {
        relationService.deleteRelation(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CompanyContactRelation> partialUpdateCompanyContactRelation(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        updates.forEach((fieldName, fieldValue) -> {
            this.relationService.partialUpdateCompanyContactRelation(id, fieldName, fieldValue);
        });
        return ResponseEntity.ok(this.relationService.getRelationById(id));
    }
}
