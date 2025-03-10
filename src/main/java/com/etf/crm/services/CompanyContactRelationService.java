package com.etf.crm.services;

import com.etf.crm.entities.CompanyContactRelation;
import com.etf.crm.enums.CompanyContactRelationType;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.repositories.CompanyContactRelationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.etf.crm.common.CrmConstants.ErrorCodes.RELATION_NOT_FOUND;

@Service
public class CompanyContactRelationService {

    @Autowired
    private CompanyContactRelationRepository relationRepository;

    @Transactional
    public CompanyContactRelation saveRelation(Long contactId, Long companyId, CompanyContactRelationType relationType) {
        CompanyContactRelation relation = CompanyContactRelation.builder()
//                .contact(contact)
                .relationType(relationType)
                .build();

        return relationRepository.save(relation);
    }

    public CompanyContactRelation getRelationById(Long id) {
        return this.relationRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(RELATION_NOT_FOUND));
    }

    public List<CompanyContactRelation> getAllRelations() {
        return this.relationRepository.findAllByDeletedFalse();
    }

    @Transactional
    public void deleteRelation(Long id) {
        CompanyContactRelation relation = this.getRelationById(id);
        relation.setDeleted(true);
        this.relationRepository.save(relation);
    }

    @Transactional
    public CompanyContactRelation updateRelation(Long id, CompanyContactRelation relation) {
        CompanyContactRelation existingRelation = this.getRelationById(id);
        existingRelation.setRelationType(relation.getRelationType());
        return this.relationRepository.save(existingRelation);
    }
}
