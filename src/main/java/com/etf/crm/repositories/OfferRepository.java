package com.etf.crm.repositories;

import com.etf.crm.dtos.OfferDto;
import com.etf.crm.entities.Offer;
import com.etf.crm.entities.User;
import com.etf.crm.enums.OfferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    Optional<Offer> findByIdAndDeletedFalse(Long id);

    List<Offer> findAllByDeletedFalse();

    @Query("""
            SELECT new com.etf.crm.dtos.OfferDto(o.id,o.name, c.id, c.name, opp.id, opp.name, con.id, con.name, o.status, createdBy.username, modifiedBy.username, o.dateCreated, o.dateModified)
            FROM Offer o
            LEFT JOIN o.company c
            LEFT JOIN o.opportunity opp
            LEFT JOIN o.contract con
            LEFT JOIN o.createdBy createdBy
            LEFT JOIN o.modifiedBy modifiedBy
            WHERE o.id = :id AND o.deleted = false""")
    Optional<OfferDto> findOfferDtoByIdAndDeletedFalse(Long id);

    @Query("""
            SELECT new com.etf.crm.dtos.OfferDto(o.id,o.name, c.id, c.name, opp.id, opp.name, con.id, con.name, o.status, createdBy.username, modifiedBy.username, o.dateCreated, o.dateModified)
            FROM Offer o
            LEFT JOIN o.company c
            LEFT JOIN o.opportunity opp
            LEFT JOIN o.contract con
            LEFT JOIN o.createdBy createdBy
            LEFT JOIN o.modifiedBy modifiedBy
            WHERE o.deleted = false""")
    List<OfferDto> findAllOfferDtoByDeletedFalse();

    @Query("""
                SELECT new com.etf.crm.dtos.OfferDto(o.id, o.name, c.id, c.name, opp.id, opp.name,
                                                     con.id, con.name, o.status, createdBy.username, modifiedBy.username,
                                                     o.dateCreated, o.dateModified)
                FROM Offer o
                LEFT JOIN o.company c
                LEFT JOIN o.opportunity opp
                LEFT JOIN o.contract con
                LEFT JOIN o.createdBy createdBy
                LEFT JOIN o.modifiedBy modifiedBy
                WHERE o.deleted = false AND opp.id = :opportunityId
            """)
    List<OfferDto> findAllOfferDtoByOpportunityIdAndDeletedFalse(Long opportunityId);

    @Query("""
                SELECT COUNT(o) > 0
                FROM Offer o
                WHERE o.opportunity.id = :opportunityId
                  AND o.id <> :offerId
                  AND o.status IN (
                    com.etf.crm.enums.OfferStatus.L1_PENDING,
                    com.etf.crm.enums.OfferStatus.L2_PENDING,
                    com.etf.crm.enums.OfferStatus.CUSTOMER_ACCEPTED,
                    com.etf.crm.enums.OfferStatus.OFFER_APPROVED,
                    com.etf.crm.enums.OfferStatus.CONCLUDED
                  )
                  AND o.deleted = false
            """)
    boolean existsActiveOfferByOpportunityId(@Param("opportunityId") Long opportunityId, @Param("offerId") long offerId);

    @Modifying
    @Query("""
                UPDATE Offer o
                SET o.status = :offerStatus,
                    o.modifiedBy = :modifiedBy,
                    o.dateModified = CURRENT_TIMESTAMP
                WHERE o.opportunity.id = :opportunityId AND o.deleted = false
            """)
    void updateOfferStatusAndModifyByViaOpportunityId(@Param("opportunityId") Long opportunityId,
                                                      @Param("offerStatus") OfferStatus offerStatus,
                                                      @Param("modifiedBy") User modifiedBy);

}
