package com.etf.crm.controllers;

import com.etf.crm.dtos.MessageResponse;
import com.etf.crm.dtos.RegionDto;
import com.etf.crm.dtos.UpdateRegionRequestDto;
import com.etf.crm.entities.Region;
import com.etf.crm.services.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/regions")
public class RegionController {

    @Autowired
    private RegionService regionService;

    @PostMapping
    public ResponseEntity<MessageResponse> saveRegion(@RequestBody Region region) {
        return ResponseEntity.ok(new MessageResponse(regionService.saveRegion(region)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegionDto> getRegionById(@PathVariable Long id) {
        return ResponseEntity.ok(regionService.getRegionById(id));
    }

    @GetMapping
    public ResponseEntity<List<RegionDto>> getFilteredAndSortedRegions(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        List<RegionDto> regions = regionService.getFilteredAndSortedRegions(name, sortBy, sortOrder);
        return ResponseEntity.ok(regions);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteRegion(@PathVariable Long id) {
        return ResponseEntity.ok(new MessageResponse(regionService.deleteRegion(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateRegion(@PathVariable Long id, @RequestBody UpdateRegionRequestDto updatedRegionData) {
        return ResponseEntity.ok(new MessageResponse(regionService.updateRegion(id, updatedRegionData)));
    }
}
