package com.etf.crm.services;

import com.etf.crm.entities.Company;
import com.etf.crm.entities.CompanyContactRelation;
import com.etf.crm.entities.Contact;
import com.etf.crm.enums.CompanyContactRelationType;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.repositories.CompanyContactRelationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;

import static com.etf.crm.common.CrmConstants.ErrorCodes.RELATION_NOT_FOUND;

@Service
public class CompanyContactRelationService {

    @Autowired
    private CompanyContactRelationRepository relationRepository;

    @Autowired
    private ContactService contactService;

    @Autowired
    private CompanyService companyService;

    public CompanyContactRelation saveRelation(Long contactId, Long companyId, CompanyContactRelationType relationType, String roleDescription) {
        Contact contact = contactService.getContactById(contactId);
        Company company = companyService.getCompanyById(companyId);

        CompanyContactRelation relation = CompanyContactRelation.builder()
                .contact(contact)
                .company(company)
                .relationType(relationType)
                .roleDescription(roleDescription)
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

    public void deleteRelation(Long id) {
        CompanyContactRelation relation = this.getRelationById(id);
        relation.setDeleted(true);
        this.relationRepository.save(relation);
    }

    public CompanyContactRelation updateRelation(Long id, CompanyContactRelation relation) {
        CompanyContactRelation existingRelation = this.getRelationById(id);
        existingRelation.setRoleDescription(relation.getRoleDescription());
        existingRelation.setRelationType(relation.getRelationType());
        return this.relationRepository.save(existingRelation);
    }

    public void partialUpdateCompanyContactRelation(Long id, String fieldName, Object fieldValue) {
        CompanyContactRelation existingRelation = this.getRelationById(id);
        try {
            Field field = CompanyContactRelation.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(existingRelation, fieldValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException("Invalid field name: " + fieldName, e);
        }
        this.relationRepository.save(existingRelation);
    }
}
