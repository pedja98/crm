package com.etf.crm.services;

import com.etf.crm.dtos.RegionDto;
import com.etf.crm.dtos.SaveRegionRequestDto;
import com.etf.crm.entities.Region;
import com.etf.crm.entities.User;
import com.etf.crm.enums.UserType;
import com.etf.crm.exceptions.DuplicateItemException;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.exceptions.UnauthorizedException;
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
    public String createRegion(Region region) {
        User currentUser = SetCurrentUserFilter.getCurrentUser();
        if (!currentUser.getType().equals(UserType.ADMIN)) {
            throw new UnauthorizedException(UNAUTHORIZED);
        }
        if (regionRepository.findByNameAndDeletedFalse(region.getName()).isPresent()) {
            throw new DuplicateItemException(REGION_ALREADY_EXISTS);
        }
        region.setCreatedBy(currentUser);
        regionRepository.save(region);
        return REGION_CREATED;
    }

    public RegionDto getRegionById(Long id) {
        User currentUser = SetCurrentUserFilter.getCurrentUser();
        if (!currentUser.getType().equals(UserType.ADMIN)) {
            throw new UnauthorizedException(UNAUTHORIZED);
        }
        return regionRepository.findRegionDtoById(id)
                .orElseThrow(() -> new ItemNotFoundException(REGION_NOT_FOUND));
    }

    public List<RegionDto> getFilteredAndSortedRegions(String name, String sortBy, String sortOrder) {
        User currentUser = SetCurrentUserFilter.getCurrentUser();
        if (!currentUser.getType().equals(UserType.ADMIN)) {
            throw new UnauthorizedException(UNAUTHORIZED);
        }

        List<RegionDto> regions = regionRepository.findAllRegionDtoByDeletedFalse();

        if (regions.isEmpty()) {
            throw new ItemNotFoundException(REGION_NOT_FOUND);
        }

        if (name != null && !name.trim().isEmpty()) {
            String filter = name.trim().toLowerCase();
            regions = regions.stream()
                    .filter(region -> region.getName() != null && region.getName().toLowerCase().contains(filter))
                    .toList();
        }

        if (sortBy != null) {
            Comparator<RegionDto> comparator = Comparator.comparing(region -> {
                try {
                    Field field = RegionDto.class.getDeclaredField(sortBy);
                    field.setAccessible(true);
                    return (Comparable) field.get(region);
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
    public String deleteRegion(Long id) {
        User currentUser = SetCurrentUserFilter.getCurrentUser();
        if (!currentUser.getType().equals(UserType.ADMIN)) {
            throw new UnauthorizedException(UNAUTHORIZED);
        }
        Region region = regionRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(REGION_NOT_FOUND));
        region.setDeleted(true);
        region.setModifiedBy(currentUser);
        regionRepository.save(region);
        return REGION_DELETED;
    }

    @Transactional
    public String updateRegion(Long id, SaveRegionRequestDto updatedRegionData) {
        User currentUser = SetCurrentUserFilter.getCurrentUser();
        if (!currentUser.getType().equals(UserType.ADMIN)) {
            throw new UnauthorizedException(UNAUTHORIZED);
        }
        if (regionRepository.findByNameAndDeletedFalse(updatedRegionData.getName()).isPresent()) {
            throw new DuplicateItemException(REGION_ALREADY_EXISTS);
        }

        Region region = regionRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(REGION_NOT_FOUND));

        region.setName(updatedRegionData.getName());
        region.setModifiedBy(SetCurrentUserFilter.getCurrentUser());
        regionRepository.save(region);
        return REGION_UPDATED;
    }
}
