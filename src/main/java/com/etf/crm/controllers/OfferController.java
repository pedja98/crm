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
        Offer savedOffer = offerService.saveOffer(companyId, offer);
        return ResponseEntity.ok(savedOffer);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Offer> getOfferById(@PathVariable Long id) {
        Offer offer = offerService.getOfferById(id);
        return ResponseEntity.ok(offer);
    }

    @GetMapping
    public ResponseEntity<List<Offer>> getAllOffers() {
        List<Offer> offers = offerService.getAllOffers();
        return ResponseEntity.ok(offers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Offer> updateOffer(@PathVariable Long id, @RequestBody Offer offerDetails) {
        Offer updatedOffer = offerService.updateOffer(id, offerDetails);
        return ResponseEntity.ok(updatedOffer);
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
        Offer updatedOffer = this.offerService.getOfferById(id);
        return ResponseEntity.ok(updatedOffer);
    }
}
