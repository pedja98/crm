package com.etf.crm.controllers;

import com.etf.crm.entities.Offer;
import com.etf.crm.services.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/offers")
public class OfferController {

    @Autowired
    private OfferService offerService;

    @PostMapping("/{companyId}")
    public ResponseEntity<Offer> createOffer(@PathVariable Long companyId, @RequestBody Offer offer) {
        return ResponseEntity.ok(offerService.saveOffer(companyId, offer));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Offer> getOfferById(@PathVariable Long id) {
        return ResponseEntity.ok(offerService.getOfferById(id));
    }

    @GetMapping
    public ResponseEntity<List<Offer>> getAllOffers() {
        return ResponseEntity.ok(offerService.getAllOffers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Offer> updateOffer(@PathVariable Long id, @RequestBody Offer offerDetails) {
        return ResponseEntity.ok(offerService.updateOffer(id, offerDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long id) {
        offerService.deleteOffer(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Offer> partialUpdate_(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        updates.forEach((fieldName, fieldValue) -> {
            this.offerService.partialUpdateOffer(id, fieldName, fieldValue);
        });
        return ResponseEntity.ok(this.offerService.getOfferById(id));
    }
}
