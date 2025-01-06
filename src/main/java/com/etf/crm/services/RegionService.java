package com.etf.crm.services;

import com.etf.crm.dtos.UserDto;
import com.etf.crm.entities.Region;
import com.etf.crm.exceptions.DuplicateItemException;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.filters.SetCurrentUserFilter;
import com.etf.crm.repositories.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;

import static com.etf.crm.common.CrmConstants.ErrorCodes.*;
import static com.etf.crm.common.CrmConstants.SuccessCodes.*;

@Service
public class RegionService {

    @Autowired
    private RegionRepository regionRepository;

    @Transactional
    public String saveRegion(Region region) {
        if (regionRepository.findByNameAndDeletedFalse(region.getName()).isPresent()) {
            throw new DuplicateItemException(REGION_ALREADY_EXISTS);
        }
        region.setCreatedBy(SetCurrentUserFilter.getCurrentUser());
        regionRepository.save(region);
        return REGION_CREATED;
    }

    public Region getRegionById(Long id) {
        return regionRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException("REGION_NOT_FOUND"));
    }

    public List<Region> getFilteredAndSortedRegions(String filterByName, String sortBy, String sortOrder) {
        List<Region> regions = regionRepository.findAllRegionsNotDeleted();

        if (regions.isEmpty()) {
            throw new ItemNotFoundException(NO_REGION_FOUND);
        }

        if (filterByName != null && !filterByName.trim().isEmpty()) {
            String filter = filterByName.trim().toLowerCase();
            regions = regions.stream()
                    .filter(region -> region.getName() != null && region.getName().toLowerCase().contains(filter))
                    .toList();
        }

        if (sortBy != null) {
            Comparator<Region> comparator = Comparator.comparing(user -> {
                try {
                    Field field = UserDto.class.getDeclaredField(sortBy);
                    field.setAccessible(true);
                    return (Comparable) field.get(user);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new IllegalArgumentException(ILLEGAL_SORT_PARAMETER + ": " + sortBy, e);
                }
            });

            if ("desc".equalsIgnoreCase(sortOrder)) {
                comparator = comparator.reversed();
            }
            regions = regions.stream().sorted(comparator).toList();
        }

        return regions;
    }


    @Transactional
    public void deleteRegion(Long id) {
        Region region = regionRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException("REGION_NOT_FOUND"));
        region.setDeleted(true);
        regionRepository.save(region);
    }
}
