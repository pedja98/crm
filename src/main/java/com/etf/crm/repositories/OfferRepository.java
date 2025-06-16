package com.etf.crm.repositories;

import com.etf.crm.dtos.OfferDto;
import com.etf.crm.entities.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    Optional<Offer> findByIdAndDeletedFalse(Long id);
    List<Offer> findAllByDeletedFalse();

    @Query("""
    SELECT new com.etf.crm.dtos.OfferDto(o.id,o.name, o.omOfferId, c.id, c.name, opp.id, opp.name, con.id, con.name, o.status, createdBy.username, modifiedBy.username, o.dateCreated, o.dateModified)
    FROM Offer o
    LEFT JOIN o.company c
    LEFT JOIN o.opportunity opp
    LEFT JOIN o.contract con
    LEFT JOIN o.createdBy createdBy
    LEFT JOIN o.modifiedBy modifiedBy
    WHERE o.id = :id AND o.deleted = false""")
    Optional<OfferDto> findOfferDtoByIdAndDeletedFalse(Long id);

    @Query("""
    SELECT new com.etf.crm.dtos.OfferDto(o.id,o.name, o.omOfferId, c.id, c.name, opp.id, opp.name, con.id, con.name, o.status, createdBy.username, modifiedBy.username, o.dateCreated, o.dateModified)
    FROM Offer o
    LEFT JOIN o.company c
    LEFT JOIN o.opportunity opp
    LEFT JOIN o.contract con
    LEFT JOIN o.createdBy createdBy
    LEFT JOIN o.modifiedBy modifiedBy
    WHERE o.deleted = false""")
    List<OfferDto> findAllOfferDtoByDeletedFalse();

}
