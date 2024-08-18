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
        CompanyContactRelation relation = relationService.saveRelation(contactId, companyId, relationType, roleDescription);
        return ResponseEntity.ok(relation);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyContactRelation> getRelationById(@PathVariable Long id) {
        CompanyContactRelation relation = relationService.getRelationById(id);
        return ResponseEntity.ok(relation);
    }

    @GetMapping
    public ResponseEntity<List<CompanyContactRelation>> getAllRelations() {
        List<CompanyContactRelation> relations = relationService.getAllRelations();
        return ResponseEntity.ok(relations);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyContactRelation> updateRelation(@PathVariable Long id, @RequestBody CompanyContactRelation relationDetails) {
        CompanyContactRelation updatedRelation = relationService.updateRelation(id, relationDetails);
        return ResponseEntity.ok(updatedRelation);
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
        CompanyContactRelation updatedRelation = this.relationService.getRelationById(id);
        return ResponseEntity.ok(updatedRelation);
    }
}
