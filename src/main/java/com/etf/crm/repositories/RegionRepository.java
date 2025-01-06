package com.etf.crm.repositories;

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

    @Query("SELECT r FROM Region r WHERE r.deleted = false")
    List<Region> findAllRegionsNotDeleted();
}
