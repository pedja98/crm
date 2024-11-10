package com.etf.crm.repositories;

import com.etf.crm.entities.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    Optional<Shop> findByIdAndDeletedFalse(Long id);
    List<Shop> findAllByDeletedFalse();
}
