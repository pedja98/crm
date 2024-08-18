package com.etf.crm.repositories;

import com.etf.crm.entities.CustomerSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerSessionRepository extends JpaRepository<CustomerSession, Long> {
    List<CustomerSession> findAllByDeletedFalse();
    Optional<CustomerSession> findByIdAndDeletedFalse(Long id);
}
