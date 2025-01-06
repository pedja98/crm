package com.etf.crm.controllers;

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
    public ResponseEntity<String> saveRegion(@RequestBody Region region) {
        String response = regionService.saveRegion(region);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Region> getRegionById(@PathVariable Long id) {
        Region region = regionService.getRegionById(id);
        return ResponseEntity.ok(region);
    }

    @GetMapping
    public ResponseEntity<List<Region>> getFilteredAndSortedRegions(
            @RequestParam(required = false) String filterByName,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        List<Region> regions = regionService.getFilteredAndSortedRegions(filterByName, sortBy, sortOrder);
        return ResponseEntity.ok(regions);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegion(@PathVariable Long id) {
        regionService.deleteRegion(id);
        return ResponseEntity.noContent().build();
    }
}
