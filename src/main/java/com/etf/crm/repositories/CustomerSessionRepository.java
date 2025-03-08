package com.etf.crm.repositories;

import com.etf.crm.dtos.CustomerSessionDto;
import com.etf.crm.entities.CustomerSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerSessionRepository extends JpaRepository<CustomerSession, Long> {
    Optional<CustomerSession> findByIdAndDeletedFalse(Long id);

    @Query("SELECT new com.etf.crm.dtos.CustomerSessionDto(cs.id, cs.name, cs.description, cs.status, cs.type, cs.mode, cs.sessionStart," +
            " cs.sessionEnd, c.id, c.name, o.id, o.name, cb.username, mb.username, c.dateCreated, c.dateModified)" +
            "FROM CustomerSession cs " +
            "LEFT JOIN cs.createdBy cb " +
            "LEFT JOIN cs.modifiedBy mb " +
            "LEFT JOIN cs.company c " +
            "LEFT JOIN cs.opportunity o " +
            "WHERE cs.id = :id AND cs.deleted = false")
    Optional<CustomerSessionDto> findCustomerSessionDtoByIdAndDeletedFalse(Long id);

    @Query("SELECT new com.etf.crm.dtos.CustomerSessionDto(cs.id, cs.name, cs.description, cs.status, cs.type, cs.mode, cs.sessionStart," +
            " cs.sessionEnd, c.id, c.name, o.id, o.name, cb.username, mb.username, c.dateCreated, c.dateModified)" +
            "FROM CustomerSession cs " +
            "LEFT JOIN cs.createdBy cb " +
            "LEFT JOIN cs.modifiedBy mb " +
            "LEFT JOIN cs.company c " +
            "LEFT JOIN cs.opportunity o " +
            "WHERE cs.deleted = false")
    Optional<List<CustomerSessionDto>> findAllCustomerSessionDtoByDeletedFalse();
}
