package com.etf.crm.services;

import com.etf.crm.dtos.CompanyContactRelationDto;
import com.etf.crm.dtos.CreateCompanyContactRelationDto;
import com.etf.crm.dtos.UpdateCompanyContactRelation;
import com.etf.crm.entities.Company;
import com.etf.crm.entities.CompanyContactRelation;
import com.etf.crm.entities.Contact;
import com.etf.crm.entities.User;
import com.etf.crm.enums.CompanyContactRelationType;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.filters.SetCurrentUserFilter;
import com.etf.crm.repositories.CompanyContactRelationRepository;
import com.etf.crm.repositories.CompanyRepository;
import com.etf.crm.repositories.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.etf.crm.common.CrmConstants.ErrorCodes.*;
import static com.etf.crm.common.CrmConstants.SuccessCodes.*;

@Service
public class CompanyContactRelationService {

    @Autowired
    private CompanyContactRelationRepository relationRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Transactional
    public String createRelation(CreateCompanyContactRelationDto relation) {
        User currentUser = SetCurrentUserFilter.getCurrentUser();
        for(CompanyContactRelationType relationType : relation.getRelationTypes()){
            Company company = companyRepository.findByIdAndDeletedFalse(relation.getCompanyId())
                    .orElseThrow(() -> new ItemNotFoundException(COMPANY_NOT_FOUND));

            Contact contact = contactRepository.findByIdAndDeletedFalse(relation.getContactId())
                    .orElseThrow(() -> new ItemNotFoundException(CONTACT_NOT_FOUND));

            CompanyContactRelation companyContactRelation = CompanyContactRelation.builder()
                    .company(company)
                    .relationType(relationType)
                    .contact(contact)
                    .createdBy(currentUser)
                    .deleted(false)
                    .build();

            this.relationRepository.save(companyContactRelation);
        }
        return ALL_RELATIONS_CREATED;
    }

    public List<CompanyContactRelationDto> getAllRelationByContactId(Long contactId) {
        return this.relationRepository.findAllCompanyContactRelationDtoByContactIdAndDeletedFalse(contactId)
                .orElseThrow(() -> new ItemNotFoundException(RELATION_NOT_FOUND));
    }

    public List<CompanyContactRelationDto> getAllRelationByCompanyId(Long companyId) {
        return this.relationRepository.findAllCompanyContactRelationDtoByCompanyIdAndDeletedFalse(companyId)
                .orElseThrow(() -> new ItemNotFoundException(RELATION_NOT_FOUND));
    }

    @Transactional
    public String deleteRelation(Long id) {
        CompanyContactRelation relation = this.relationRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(RELATION_NOT_FOUND));
        relation.setDeleted(true);
        relation.setModifiedBy(SetCurrentUserFilter.getCurrentUser());
        this.relationRepository.save(relation);
        return RELATION_DELETED;
    }

    @Transactional
    public String updateRelation(Long id, UpdateCompanyContactRelation updateRelationData) {
        CompanyContactRelation relation = this.relationRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(RELATION_NOT_FOUND));

        Company company = companyRepository.findByIdAndDeletedFalse(updateRelationData.getCompanyId())
                .orElseThrow(() -> new ItemNotFoundException(COMPANY_NOT_FOUND));
        relation.setModifiedBy(SetCurrentUserFilter.getCurrentUser());
        relation.setCompany(company);
        relation.setRelationType(updateRelationData.getRelationType());

        this.relationRepository.save(relation);
        return RELATION_UPDATED;
    }
}
