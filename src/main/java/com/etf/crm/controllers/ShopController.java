package com.etf.crm.controllers;

import com.etf.crm.dtos.MessageResponse;
import com.etf.crm.dtos.SaveShopDto;
import com.etf.crm.dtos.ShopDto;
import com.etf.crm.services.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shops")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @GetMapping("/{id}")
    public ResponseEntity<ShopDto> getCompanyById(@PathVariable Long id) {
        return ResponseEntity.ok(this.shopService.getShopById(id));
    }

    @GetMapping
    public ResponseEntity<List<ShopDto>> getAllShops(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortOrder,
            @RequestParam(required = false, value = "region") List<Long> regions,
            @RequestParam(required = false, value = "shopLeader") List<Long> shopLeaders,
            @RequestParam(required = false) String name
    ) {
        return ResponseEntity.ok(this.shopService.getFilteredAndSortedShops(sortBy, sortOrder, regions, shopLeaders, name));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteShop(@PathVariable Long id) {
        return ResponseEntity.ok(new MessageResponse(this.shopService.deleteShop(id)));
    }

    @PostMapping
    public ResponseEntity<MessageResponse> createShop(@RequestBody SaveShopDto shopData) {
        return ResponseEntity.ok(new MessageResponse(this.shopService.createShop(shopData)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateCompany(@PathVariable Long id, @RequestBody SaveShopDto shopDetails) {
        return ResponseEntity.ok(new MessageResponse(this.shopService.updateShop(id, shopDetails)));
    }
}
