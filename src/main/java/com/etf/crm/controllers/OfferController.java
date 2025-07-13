package com.etf.crm.controllers;

import com.etf.crm.dtos.CreateCrmOfferResponseDto;
import com.etf.crm.dtos.CreateOfferDto;
import com.etf.crm.dtos.MessageResponse;
import com.etf.crm.dtos.OfferDto;
import com.etf.crm.enums.OfferStatus;
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

    @PostMapping
    public ResponseEntity<CreateCrmOfferResponseDto> createOffer(@RequestBody CreateOfferDto body) {
        return ResponseEntity.ok(offerService.createOffer(body));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfferDto> getOfferById(@PathVariable Long id) {
        return ResponseEntity.ok(offerService.getOfferById(id));
    }

    @GetMapping
    public ResponseEntity<List<OfferDto>> getAllOffers(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) String name,
            @RequestParam(required = false, value = "status") List<OfferStatus> statuses,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) Long opportunityId) {
        return ResponseEntity.ok(offerService.getAllOffers(sortBy, sortOrder, name, statuses, companyId, opportunityId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MessageResponse> patchOffer(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body
    ) {
        return ResponseEntity.ok(new MessageResponse(offerService.patchOffer(id, body)));
    }
}
