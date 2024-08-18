package com.etf.crm.repositories;

import com.etf.crm.entities.Opportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, Long> {
    Optional<Opportunity> findByIdAndDeletedFalse(Long id);
    List<Opportunity> findAllByDeletedFalse();
}
