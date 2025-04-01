package com.etf.crm.repositories;

import com.etf.crm.dtos.OpportunityDto;
import com.etf.crm.entities.Opportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, Long> {
    Optional<Opportunity> findByIdAndDeletedFalse(Long id);

    @Query("SELECT new com.etf.crm.dtos.OpportunityDto(o.id, o.name, o.status, o.type, c.id, c.name, cb.username, mb.username, o.dateCreated, o.dateModified)" +
            "FROM Opportunity o " +
            "LEFT JOIN o.createdBy cb " +
            "LEFT JOIN o.modifiedBy mb " +
            "LEFT JOIN o.company c " +
            "WHERE o.id = :id AND o.deleted = false")
    Optional<OpportunityDto> findOpportunityDtoByIdAndDeletedFalse(Long id);

    @Query("SELECT new com.etf.crm.dtos.OpportunityDto(o.id, o.name, o.status, o.type, c.id, c.name, cb.username, mb.username, o.dateCreated, o.dateModified)" +
            "FROM Opportunity o " +
            "LEFT JOIN o.createdBy cb " +
            "LEFT JOIN o.modifiedBy mb " +
            "LEFT JOIN o.company c " +
            "WHERE o.deleted = false")
    Optional<List<OpportunityDto>> findAllOpportunityDtoByDeletedFalse();
}
