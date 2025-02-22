package com.etf.crm.repositories;

import com.etf.crm.dtos.ContactDto;
import com.etf.crm.entities.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    Optional<Contact> findByIdAndDeletedFalse(Long id);
    List<Contact> findAllByDeletedFalse();

    @Query("SELECT new com.etf.crm.dtos.ContactDto(c.id, c.firstName, c.lastName, c.email, c.phone, c.documentType, c.documentId, "
            + "cb.username, mb.username, c.dateCreated, c.dateModified)" +
            "FROM Contact c " +
            "LEFT JOIN c.createdBy cb " +
            "LEFT JOIN c.modifiedBy mb " +
            "WHERE c.id = :id AND c.deleted = false")
    Optional<ContactDto> findContactDtoByIdAndDeletedFalse(Long id);

    @Query("SELECT new com.etf.crm.dtos.ContactDto(c.id, c.firstName, c.lastName, c.email, c.phone, c.documentType, c.documentId, "
            + "cb.username, mb.username, c.dateCreated, c.dateModified)" +
            "FROM Contact c " +
            "LEFT JOIN c.createdBy cb " +
            "LEFT JOIN c.modifiedBy mb " +
            "WHERE c.deleted = false")
    Optional<List<ContactDto>> findAllContactDtoByIdAndDeletedFalse();
}
