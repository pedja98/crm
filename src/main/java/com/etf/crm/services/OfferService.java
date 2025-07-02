package com.etf.crm.services;

import com.etf.crm.dtos.CreateCrmOfferResponseDto;
import com.etf.crm.dtos.CreateOfferDto;
import com.etf.crm.dtos.OfferDto;
import com.etf.crm.entities.Company;
import com.etf.crm.entities.Offer;
import com.etf.crm.entities.Opportunity;
import com.etf.crm.enums.OfferStatus;
import com.etf.crm.enums.OpportunityStatus;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.filters.SetCurrentUserFilter;
import com.etf.crm.repositories.CompanyRepository;
import com.etf.crm.repositories.OfferRepository;
import com.etf.crm.repositories.OpportunityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.etf.crm.common.CrmConstants.ErrorCodes.*;

@Service
public class OfferService {

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Transactional
    public CreateCrmOfferResponseDto createOffer(CreateOfferDto body) {
        Opportunity opportunity = this.opportunityRepository.findById(body.getOpportunityId())
                .orElseThrow(() -> new ItemNotFoundException(OPPORTUNITY_NOT_FOUND));

        Company company = this.companyRepository.findById(body.getCompanyId())
                .orElseThrow(() -> new ItemNotFoundException(COMPANY_NOT_FOUND));

        Offer offer = Offer.builder()
                .company(company)
                .name(body.getName())
                .opportunity(opportunity)
                .contract(null)
                .status(OfferStatus.DRAFT)
                .createdBy(SetCurrentUserFilter.getCurrentUser())
                .deleted(false)
                .build();

        opportunity.setStatus(OpportunityStatus.NEGOTIATIONS);

        return new CreateCrmOfferResponseDto(this.offerRepository.save(offer).getId());
    }

    public OfferDto getOfferById(Long id) {
        return this.offerRepository.findOfferDtoByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(OFFER_NOT_FOUND));
    }

    public List<OfferDto> getAllOffers(String sortBy, String sortOrder, String name, List<OfferStatus> statuses) {
        List<OfferDto> offers = offerRepository.findAllOfferDtoByDeletedFalse();

        Map<String, Object> filters = new HashMap<>();
        filters.put("name", name);
        filters.put("status", statuses);

        List<Predicate<OfferDto>> predicates = filters.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .map(entry -> {
                    String fieldName = entry.getKey();
                    Object value = entry.getValue();

                    return (Predicate<OfferDto>) offer -> {
                        try {
                            Field field = OfferDto.class.getDeclaredField(fieldName);
                            field.setAccessible(true);
                            Object fieldValue = field.get(offer);

                            if (value instanceof String stringValue) {
                                return fieldValue != null && fieldValue.toString().toLowerCase().contains(stringValue.toLowerCase());
                            } else if (value instanceof List<?> listValue) {
                                return fieldValue != null && listValue.contains(fieldValue);
                            }
                            return false;
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            throw new RuntimeException("Invalid filter field: " + fieldName, e);
                        }
                    };
                })
                .toList();

        for (Predicate<OfferDto> predicate : predicates) {
            offers = offers.stream().filter(predicate).toList();
        }

        if (sortBy != null) {
            Comparator<OfferDto> comparator = Comparator.comparing(offer -> {
                try {
                    Field field = OfferDto.class.getDeclaredField(sortBy);
                    field.setAccessible(true);
                    return (Comparable) field.get(offer);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new IllegalArgumentException("Invalid sort field: " + sortBy, e);
                }
            });

            if ("desc".equalsIgnoreCase(sortOrder)) {
                comparator = comparator.reversed();
            }

            offers = offers.stream().sorted(comparator).toList();
        }

        return offers;
    }

    public List<OfferDto> getOffersByOpportunityId(Long opportunityId) {
        return offerRepository.findAllOfferDtoByOpportunityIdAndDeletedFalse(opportunityId);
    }

    @Transactional
    public String patchOffer(Long id, Map<String, Object> updates) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(OFFER_NOT_FOUND));

        Set<String> allowedFields = Arrays.stream(Offer.class.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toSet());

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (!allowedFields.contains(key)) {
                throw new IllegalArgumentException("Field '" + key + "' is not allowed to be updated");
            }

            if (key.equals("status")
                    && Arrays.stream(new OfferStatus[]{OfferStatus.L1_PENDING, OfferStatus.L2_PENDING})
                    .anyMatch(status -> status.name().equals(value))
                    && this.offerRepository.existsActiveOfferByOpportunityId(offer.getOpportunity().getId(), offer.getId())) {
                throw new RuntimeException(OFFER_STATUS_TRANSITION_NOT_POSSIBLE);
            }

            try {
                Field field = Offer.class.getDeclaredField(key);
                field.setAccessible(true);

                Class<?> fieldType = field.getType();
                Object convertedValue;

                if (fieldType.equals(String.class)) {
                    convertedValue = value.toString();
                } else if (fieldType.equals(int.class) || fieldType.equals(Integer.class)) {
                    convertedValue = Integer.parseInt(value.toString());
                } else if (fieldType.isEnum()) {
                    convertedValue = Enum.valueOf((Class<Enum>) fieldType, value.toString());
                } else {
                    throw new IllegalArgumentException("Unsupported field type: " + fieldType.getName());
                }

                field.set(offer, convertedValue);

            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException("Field '" + key + "' does not exist on Offer");
            } catch (IllegalAccessException | IllegalArgumentException e) {
                throw new RuntimeException("Failed to set field '" + key + "'", e);
            }
        }
        offer.setModifiedBy(SetCurrentUserFilter.getCurrentUser());
        offerRepository.save(offer);
        return OFFER_UPDATED;
    }
}
