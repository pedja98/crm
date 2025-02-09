package com.etf.crm.repositories;

import com.etf.crm.dtos.ShopDto;
import com.etf.crm.entities.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    Optional<Shop> findByIdAndDeletedFalse(Long id);
    List<Shop> findAllByDeletedFalse();

    @Query("SELECT new com.etf.crm.dtos.ShopDto(s.id, s.name, s.address, u.id, u.username, r.id, r.name, cb.id, cb.username, mb.id, mb.username, s.dateCreated, s.dateModified) " +
            "FROM Shop s " +
            "LEFT JOIN s.shopLeader u " +
            "LEFT JOIN s.createdBy cb " +
            "LEFT JOIN s.modifiedBy mb " +
            "LEFT JOIN s.region r " +
            "WHERE s.id = :id AND s.deleted = false")
    Optional<ShopDto> findShopDtoByUsernameAndDeletedFalse(@Param("id") Long id);

    @Query("SELECT new com.etf.crm.dtos.ShopDto(s.id, s.name, s.address, u.id, u.username, r.id, r.name, cb.id, cb.username, mb.id, mb.username, s.dateCreated, s.dateModified) " +
            "FROM Shop s " +
            "LEFT JOIN s.shopLeader u " +
            "LEFT JOIN s.createdBy cb " +
            "LEFT JOIN s.modifiedBy mb " +
            "LEFT JOIN s.region r " +
            "WHERE s.deleted = false")
    Optional<List<ShopDto>> findAllShopDtoByDeletedFalse();
}
