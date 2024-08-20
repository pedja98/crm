package com.etf.crm.repositories;

import com.etf.crm.entities.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    Optional<Contact> findByIdAndDeletedFalse(Long id);
    List<Contact> findAllByDeletedFalse();
    Optional<Contact> findByEmailAndDeletedFalse(String email);
}
