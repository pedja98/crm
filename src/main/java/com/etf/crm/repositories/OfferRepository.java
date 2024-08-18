package com.etf.crm.repositories;

import com.etf.crm.entities.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    Optional<Offer> findByIdAndDeletedFalse(Long id);
    List<Offer> findAllByDeletedFalse();
}
