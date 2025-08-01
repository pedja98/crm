package com.etf.crm.services;

import com.etf.crm.dtos.OpportunityDto;
import com.etf.crm.entities.Company;
import com.etf.crm.entities.Opportunity;
import com.etf.crm.entities.User;
import com.etf.crm.enums.*;
import com.etf.crm.exceptions.BadRequestException;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.filters.SetCurrentUserFilter;
import com.etf.crm.repositories.CompanyRepository;
import com.etf.crm.repositories.ContractRepository;
import com.etf.crm.repositories.OfferRepository;
import com.etf.crm.repositories.OpportunityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;

import static com.etf.crm.common.CrmConstants.SuccessCodes.*;
import static com.etf.crm.common.CrmConstants.ErrorCodes.*;

@Service
public class OpportunityService {

    @Autowired
    private OmOfferService omOfferService;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private AuthorizationService authorizationService;

    public OpportunityDto getOpportunityById(Long id) {
        OpportunityDto opportunityDto = this.opportunityRepository.findOpportunityDtoByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(OPPORTUNITY_NOT_FOUND));
        this.authorizationService.isUserAuthorizedForAction(opportunityDto.getCompanyId());
        return opportunityDto;
    }

    public Opportunity createOpportunity(Company company, OpportunityType opportunityType, CustomerSessionOutcome outcome, CustomerSessionStatus status) {
        if (opportunityType == null || !outcome.equals(CustomerSessionOutcome.NEW_OFFER) || !status.equals(CustomerSessionStatus.HELD)) {
            return null;
        }

        if ((company.getStatus().equals(CompanyStatus.POTENTIAL) || company.getStatus().equals(CompanyStatus.INACTIVE)) && !opportunityType.equals(OpportunityType.ACQUISITION)) {
            throw new BadRequestException(CAN_NOT_CREATE_OPPORTUNITY_FOR_COMPANY_IN_THIS_STATUS);
        }

        if (company.getStatus().equals(CompanyStatus.ACTIVE) && opportunityType.equals(OpportunityType.ACQUISITION)) {
            throw new BadRequestException(CAN_NOT_CREATE_OPPORTUNITY_FOR_COMPANY_IN_THIS_STATUS);
        }

        this.authorizationService.isUserAuthorizedForAction(company.getId());

        Opportunity opportunity = Opportunity.builder()
                .name("OPP " + company.getName() + " " + (new SimpleDateFormat("dd/MM/yyyy")).format(new Date()))
                .type(opportunityType)
                .status(OpportunityStatus.CREATED)
                .createdBy(SetCurrentUserFilter.getCurrentUser())
                .deleted(false)
                .company(company)
                .build();

        return this.opportunityRepository.save(opportunity);
    }

    public List<OpportunityDto> getAllOpportunities(
            String sortBy,
            String sortOrder,
            String name,
            List<OpportunityType> types,
            List<OpportunityStatus> statuses,
            Long companyId) {
        List<OpportunityDto> opportunities = authorizationService
                .filterByUserAccess(opportunityRepository.findAllOpportunityDtoByDeletedFalse(), OpportunityDto::getCompanyId);

        Map<String, Object> filters = new HashMap<>();
        filters.put("name", name);
        filters.put("status", statuses);
        filters.put("type", types);
        filters.put("companyId", companyId);

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
                            } else if (value instanceof Long longValue) {
                                return fieldValue != null && fieldValue.equals(longValue);
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

        this.authorizationService.isUserAuthorizedForAction(opportunity.getCompany().getId());

        User currentUser = SetCurrentUserFilter.getCurrentUser();

        if (opportunity.getStatus().equals(OpportunityStatus.CLOSE_LOST) || opportunity.getStatus().equals(OpportunityStatus.CLOSE_WON)) {
            throw new BadRequestException(NOT_EDITABLE);
        }
        opportunity.setStatus(OpportunityStatus.CLOSE_LOST);
        opportunity.setModifiedBy(currentUser);

        this.opportunityRepository.save(opportunity);
        this.omOfferService.closeAllOmOffersConnectedToOpportunity(id);
        this.offerRepository.updateOfferStatusAndModifyByViaOpportunityId(id, OfferStatus.SALESMEN_CLOSED, currentUser);
        this.contractRepository.updateContractStatusByOpportunityId(id, ContractStatus.SALESMAN_CLOSED, currentUser);

        return OPPORTUNITY_CLOSED;
    }
}
