package com.etf.crm.controllers;

import com.etf.crm.dtos.CreateOfferDto;
import com.etf.crm.dtos.MessageResponse;
import com.etf.crm.entities.Offer;
import com.etf.crm.services.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/offers")
public class OfferController {

    @Autowired
    private OfferService offerService;

    @PostMapping("/")
    public ResponseEntity<MessageResponse> createOffer(@RequestBody CreateOfferDto offerDetails) {
        return ResponseEntity.ok(new MessageResponse(offerService.createOffer(offerDetails)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Offer> getOfferById(@PathVariable Long id) {
        return ResponseEntity.ok(offerService.getOfferById(id));
    }

    @GetMapping
    public ResponseEntity<List<Offer>> getAllOffers() {
        return ResponseEntity.ok(offerService.getAllOffers());
    }
}
