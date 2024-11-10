package com.etf.crm.controllers;

import com.etf.crm.entities.Shop;
import com.etf.crm.services.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/shops")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @GetMapping("/{id}")
    public ResponseEntity<Shop> getCompanyById(@PathVariable Long id) {
        return ResponseEntity.ok(this.shopService.getShopById(id));
    }

    @GetMapping
    public ResponseEntity<List<Shop>> getAllShops() {
        return ResponseEntity.ok(this.shopService.getAllShops());
    }
}
