package com.etf.crm.services;

import com.etf.crm.dtos.OpportunityDto;
import com.etf.crm.entities.Opportunity;
import com.etf.crm.enums.OpportunityStatus;
import com.etf.crm.enums.OpportunityType;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.filters.SetCurrentUserFilter;
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

import static com.etf.crm.common.CrmConstants.SuccessCodes.*;
import static com.etf.crm.common.CrmConstants.ErrorCodes.*;

@Service
public class OpportunityService {

    @Autowired
    private OpportunityRepository opportunityRepository;

    public OpportunityDto getOpportunityById(Long id) {
        return this.opportunityRepository.findOpportunityDtoByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(OPPORTUNITY_NOT_FOUND));
    }

    public List<OpportunityDto> getAllOpportunities(
            String sortBy,
            String sortOrder,
            String name,
            List<OpportunityType> types,
            List<OpportunityStatus> statuses) {
        List<OpportunityDto> opportunities =  this.opportunityRepository.findAllOpportunityDtoByDeletedFalse()
                .orElseThrow(() -> new ItemNotFoundException(OPPORTUNITY_NOT_FOUND));

        Map<String, Object> filters = new HashMap<>();
        filters.put("name", name);
        filters.put("status", statuses);
        filters.put("type", types);

        List<Predicate<OpportunityDto>> predicates = filters.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .map(entry -> {
                    String fieldName = entry.getKey();
                    Object value = entry.getValue();

                    return (Predicate<OpportunityDto>) opportunity -> {
                        try {
                            Field field = OpportunityDto.class.getDeclaredField(fieldName);
                            field.setAccessible(true);
                            Object fieldValue = field.get(opportunity);

                            if (value instanceof String stringValue) {
                                return fieldValue != null && fieldValue.toString().toLowerCase().contains(stringValue.toLowerCase());
                            } else if (value instanceof List<?> listValue) {
                                return fieldValue != null && listValue.contains(fieldValue);
                            }
                            return false;
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            throw new RuntimeException(ILLEGAL_SORT_PARAMETER + ": " + fieldName, e);
                        }
                    };
                })
                .toList();

        for (Predicate<OpportunityDto> predicate : predicates) {
            opportunities = opportunities.stream().filter(predicate).toList();
        }

        if (sortBy != null) {
            Comparator<OpportunityDto> comparator = Comparator.comparing(opportunity -> {
                try {
                    Field field = OpportunityDto.class.getDeclaredField(sortBy);
                    field.setAccessible(true);
                    return (Comparable) field.get(opportunity);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new IllegalArgumentException(ILLEGAL_SORT_PARAMETER + ": " + sortBy, e);
                }
            });

            if ("desc".equalsIgnoreCase(sortOrder)) {
                comparator = comparator.reversed();
            }
            opportunities = opportunities.stream().sorted(comparator).toList();
        }
        return opportunities;
    }

    @Transactional
    public String closeOpportunity(Long id) {
        Opportunity opportunity = this.opportunityRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(OPPORTUNITY_NOT_FOUND));
        opportunity.setStatus(OpportunityStatus.CLOSE_LOST);
        opportunity.setModifiedBy(SetCurrentUserFilter.getCurrentUser());
        this.opportunityRepository.save(opportunity);
        return OPPORTUNITY_CLOSED;
    }
}
