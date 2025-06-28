package com.etf.crm.repositories;

import com.etf.crm.entities.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Query("SELECT d FROM Document d WHERE d.contract.id = :contractId AND d.deleted = false")
    List<Document> findByContractIdAndDeletedFalse(@Param("contractId") Long contractId);

    @Query("SELECT d FROM Document d WHERE d.id = :id AND d.deleted = false")
    Optional<Document> findByIdAndDeletedFalse(@Param("id") Long id);

    @Query("SELECT d FROM Document d WHERE d.deleted = false")
    List<Document> findAllNotDeleted();
}