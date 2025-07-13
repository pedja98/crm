package com.etf.crm.repositories;

import com.etf.crm.dtos.ContractDto;
import com.etf.crm.dtos.ContractReportDto;
import com.etf.crm.entities.Contract;
import com.etf.crm.entities.User;
import com.etf.crm.enums.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    Optional<Contract> findByIdAndDeletedFalse(Long id);

    boolean existsByCompanyIdAndStatusAndDeletedFalse(Long companyId, ContractStatus status);

    List<Contract> findAllByDeletedFalse();

    @Query("""
            SELECT new com.etf.crm.dtos.ContractDto(c.id, c.name, opp.type, c.dateSigned,c.referenceNumber, c.contractObligation, c.status, 
                comp.id, comp.name, opp.id, opp.name, o.id, o.name,cb.username, mb.username, c.dateCreated, c.dateModified)
            FROM Contract c
            LEFT JOIN c.company comp
            LEFT JOIN c.opportunity opp
            LEFT JOIN c.offer o
            LEFT JOIN c.createdBy cb 
            LEFT JOIN c.modifiedBy mb 
            WHERE c.deleted = false
            """)
    List<ContractDto> findAllContractDtoByDeletedFalse();

    @Query("""
            SELECT new com.etf.crm.dtos.ContractDto(c.id, c.name, opp.type, c.dateSigned, c.referenceNumber, c.contractObligation, c.status, 
                comp.id, comp.name, opp.id, opp.name, o.id, o.name, cb.username, mb.username, c.dateCreated, c.dateModified)
            FROM Contract c
            LEFT JOIN c.company comp
            LEFT JOIN c.opportunity opp
            LEFT JOIN c.offer o
            LEFT JOIN c.createdBy cb 
            LEFT JOIN c.modifiedBy mb 
            WHERE c.deleted = false and c.id = :id
            """)
    Optional<ContractDto> findContractDtoByIdDeletedFalse(@Param("id") Long id);

    @Modifying
    @Query("""
                UPDATE Contract c
                SET c.status = :contractStatus,
                    c.modifiedBy = :modifiedBy,
                    c.dateModified = CURRENT_TIMESTAMP
                WHERE c.opportunity.id = :opportunityId AND c.deleted = false
            """)
    void updateContractStatusByOpportunityId(@Param("opportunityId") Long opportunityId,
                                             @Param("contractStatus") ContractStatus contractStatus,
                                             @Param("modifiedBy") User modifiedBy);

    @Query("""
            SELECT new com.etf.crm.dtos.ContractReportDto(
                c.id, c.name, c.referenceNumber, c.dateSigned, c.status, 
                comp.id, comp.name, 
                opp.id, opp.name, opp.type, 
                u.shop.id, u.shop.name, 
                u.shop.region.id, u.shop.region.name
            )
            FROM Contract c
            LEFT JOIN c.company comp
            LEFT JOIN c.opportunity opp
            LEFT JOIN comp.assignedTo u
            LEFT JOIN u.shop s
            LEFT JOIN s.region r
            WHERE c.deleted = false
            """)
    List<ContractReportDto> findAllContractReportDtoByDeletedFalse();

    @Query("""
        SELECT c FROM Contract c
        WHERE c.company.id = :companyId AND c.deleted = false
        ORDER BY c.dateSigned DESC
        LIMIT 1
    """)
    Optional<Contract> findLastSignedContractByCompanyId(@Param("companyId") Long companyId);

}
