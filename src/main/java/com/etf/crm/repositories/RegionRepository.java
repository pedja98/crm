package com.etf.crm.repositories;

import com.etf.crm.dtos.RegionDto;
import com.etf.crm.entities.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    Optional<Region> findByNameAndDeletedFalse(String name);
    Optional<Region> findByIdAndDeletedFalse(Long id);

    @Query("SELECT new com.etf.crm.dtos.RegionDto( " +
            "r.id, r.name, r.createdBy.id, r.createdBy.username, " +
            "r.modifiedBy.id, r.modifiedBy.username, " +
            "r.dateCreated, r.dateModified) " +
            "FROM Region r LEFT JOIN r.createdBy cb LEFT JOIN r.modifiedBy mb WHERE r.deleted = false")
    List<RegionDto> findAllRegionDtoByDeletedFalse();

    @Query("SELECT new com.etf.crm.dtos.RegionDto( " +
            "r.id, r.name, r.createdBy.id, r.createdBy.username, " +
            "r.modifiedBy.id, r.modifiedBy.username, " +
            "r.dateCreated, r.dateModified) " +
            "FROM Region r " +
            "LEFT JOIN r.createdBy cb " +
            "LEFT JOIN r.modifiedBy mb " +
            "WHERE r.id = :id AND r.deleted = false")
    Optional<RegionDto> findRegionDtoById(Long id);
}
