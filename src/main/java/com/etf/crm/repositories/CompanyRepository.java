package com.etf.crm.repositories;

import com.etf.crm.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByIdAndDeletedFalse(Long id);
    List<Company> findAllByDeletedFalse();
}