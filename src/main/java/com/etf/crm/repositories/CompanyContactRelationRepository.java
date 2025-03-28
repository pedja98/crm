package com.etf.crm.repositories;

import com.etf.crm.dtos.CompanyContactRelationDto;
import com.etf.crm.entities.CompanyContactRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyContactRelationRepository extends JpaRepository<CompanyContactRelation, Long> {
    Optional<CompanyContactRelation> findByIdAndDeletedFalse(Long id);

    @Query("SELECT new com.etf.crm.dtos.CompanyContactRelationDto(ccr.id, c.id, c.name, ccr.relationType, " +
            "cb.username, mb.username, ccr.dateCreated, ccr.dateModified)" +
            "FROM CompanyContactRelation ccr " +
            "LEFT JOIN ccr.createdBy cb " +
            "LEFT JOIN ccr.modifiedBy mb " +
            "LEFT JOIN ccr.company c " +
            "WHERE ccr.contact.id = :contactId AND ccr.deleted = false")
    Optional<List<CompanyContactRelationDto>> findAllCompanyContactRelationDtoByContactIdAndDeletedFalse(Long contactId);
}
