package com.etf.crm.repositories;

import com.etf.crm.entities.CompanyContactRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyContactRelationRepository extends JpaRepository<CompanyContactRelation, Long> {
    Optional<CompanyContactRelation> findByIdAndDeletedFalse(Long id);
    List<CompanyContactRelation> findAllByDeletedFalse();
}
