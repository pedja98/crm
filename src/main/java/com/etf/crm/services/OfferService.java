package com.etf.crm.services;

import com.etf.crm.dtos.CreateOfferDto;
import com.etf.crm.dtos.OfferDto;
import com.etf.crm.entities.Company;
import com.etf.crm.entities.Offer;
import com.etf.crm.entities.Opportunity;
import com.etf.crm.enums.OfferStatus;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.filters.SetCurrentUserFilter;
import com.etf.crm.repositories.CompanyRepository;
import com.etf.crm.repositories.OfferRepository;
import com.etf.crm.repositories.OpportunityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static com.etf.crm.common.CrmConstants.ErrorCodes.*;
import static com.etf.crm.common.CrmConstants.SuccessCodes.OFFER_CREATED;

@Service
public class OfferService {

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Transactional
    public String createOffer(CreateOfferDto offerDetails) {
        Opportunity opportunity = this.opportunityRepository.findById(offerDetails.getOpportunityId())
                .orElseThrow(() -> new ItemNotFoundException(OPPORTUNITY_NOT_FOUND));

        Company company = this.companyRepository.findById(offerDetails.getCompanyId())
                .orElseThrow(() -> new ItemNotFoundException(COMPANY_NOT_FOUND));

        Offer offer = Offer.builder()
                .company(company)
                .name(offerDetails.getName())
                .omOfferId(offerDetails.getOmOfferId())
                .opportunity(opportunity)
                .contract(null)
                .status(OfferStatus.DRAFT)
                .createdBy(SetCurrentUserFilter.getCurrentUser())
                .deleted(false)
                .build();

        this.offerRepository.save(offer);
        return OFFER_CREATED;
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
}
