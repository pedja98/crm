package com.etf.crm.repositories;

import com.etf.crm.dtos.CompanyDto;
import com.etf.crm.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByIdAndDeletedFalse(Long id);
    List<Company> findAllByDeletedFalse();
    Optional<Company> findByTinAndDeletedFalse(Integer tin);

    @Query("SELECT new com.etf.crm.dtos.CompanyDto(c.id, c.name, c.hqAddress, c.contactPhone, " +
            "c.numberOfEmployees, c.tin, c.bankName, c.bankAccountNumber, c.comment, c.status, ass.id, ass.username, tas.id, tas.username, " +
            " cb.id, cb.username, mb.id, mb.username, c.dateCreated, c.dateModified)" +
            "FROM Company c " +
            "LEFT JOIN c.createdBy cb " +
            "LEFT JOIN c.modifiedBy mb " +
            "LEFT JOIN c.assignedTo ass " +
            "LEFT JOIN c.temporaryAssignedTo tas " +
            "WHERE c.id = :id AND c.deleted = false")
    Optional<CompanyDto> findCompanyDtoByIdAndDeletedFalse(Long id);

    @Query("SELECT new com.etf.crm.dtos.CompanyDto(c.id, c.name, c.hqAddress, c.contactPhone, " +
            "c.numberOfEmployees, c.tin, c.bankName, c.bankAccountNumber, c.comment, c.status, ass.id, ass.username, tas.id, tas.username, " +
            " cb.id, cb.username, mb.id, mb.username, c.dateCreated, c.dateModified)" +
            "FROM Company c " +
            "LEFT JOIN c.createdBy cb " +
            "LEFT JOIN c.modifiedBy mb " +
            "LEFT JOIN c.assignedTo ass " +
            "LEFT JOIN c.temporaryAssignedTo tas " +
            "WHERE c.deleted = false")
    Optional<List<CompanyDto>> findAllCompanyDtoByDeletedFalse();
}