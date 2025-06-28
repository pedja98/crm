package com.etf.crm.services;

import com.etf.crm.dtos.ContractDto;
import com.etf.crm.dtos.CreateContractDto;
import com.etf.crm.entities.Contract;
import com.etf.crm.entities.Offer;
import com.etf.crm.entities.Opportunity;
import com.etf.crm.enums.ContractStatus;
import com.etf.crm.enums.OfferStatus;
import com.etf.crm.exceptions.BadRequestException;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.filters.SetCurrentUserFilter;
import com.etf.crm.repositories.ContractRepository;
import com.etf.crm.repositories.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.etf.crm.common.CrmConstants.SuccessCodes.*;
import static com.etf.crm.common.CrmConstants.ErrorCodes.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;

import static com.etf.crm.common.CrmConstants.ErrorCodes.CONTRACT_NOT_FOUND;

@Service
public class ContractService {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private OfferRepository offerRepository;

    public String createContract(CreateContractDto body) {
        Offer offer = this.offerRepository.findById(body.getOfferId())
                .orElseThrow(() -> new ItemNotFoundException(OFFER_NOT_FOUND));

        if (!OfferStatus.OFFER_APPROVED.equals(offer.getStatus())) {
            throw new BadRequestException(INVALID_OFFER_STATUS);
        }

        String refNo = UUID.randomUUID().toString();

        Contract contract = Contract.builder()
                .contractObligation(body.getContractObligation())
                .createdBy(SetCurrentUserFilter.getCurrentUser())
                .deleted(false)
                .name(offer.getName().replace("Offer", "Contract"))
                .company(offer.getCompany())
                .opportunity(offer.getOpportunity())
                .referenceNumber(refNo)
                .offer(offer)
                .status(ContractStatus.CREATED)
                .build();
        offer.setContract(this.contractRepository.save(contract));
        this.offerRepository.save(offer);
        return CREATE_CONTRACT;
    }

    public List<ContractDto> getAllContracts(String sortBy, String sortOrder, String name, String referenceNumber, List<ContractStatus> statuses) {
        List<ContractDto> contracts = contractRepository.findAllContractDtoByDeletedFalse();
        return contracts;
//        Map<String, Object> filters = new HashMap<>();
//        filters.put("name", name);
//        filters.put("referenceNumber", referenceNumber);
//        filters.put("status", statuses);
//
//        List<Predicate<ContractDto>> predicates = filters.entrySet().stream()
//                .filter(entry -> entry.getValue() != null)
//                .map(entry -> {
//                    String fieldName = entry.getKey();
//                    Object value = entry.getValue();
//
//                    return (Predicate<ContractDto>) contract -> {
//                        try {
//                            Field field = ContractDto.class.getDeclaredField(fieldName);
//                            field.setAccessible(true);
//                            Object fieldValue = field.get(contract);
//
//                            if (value instanceof String stringValue) {
//                                return fieldValue != null && fieldValue.toString().toLowerCase().contains(stringValue.toLowerCase());
//                            } else if (value instanceof List<?> listValue) {
//                                return fieldValue != null && listValue.contains(fieldValue);
//                            }
//                            return false;
//                        } catch (NoSuchFieldException | IllegalAccessException e) {
//                            throw new RuntimeException("Invalid filter field: " + fieldName, e);
//                        }
//                    };
//                })
//                .toList();
//
//        for (Predicate<ContractDto> predicate : predicates) {
//            contracts = contracts.stream().filter(predicate).toList();
//        }
//
//        if (sortBy != null) {
//            Comparator<ContractDto> comparator = Comparator.comparing(contract -> {
//                try {
//                    Field field = ContractDto.class.getDeclaredField(sortBy);
//                    field.setAccessible(true);
//                    return (Comparable) field.get(contract);
//                } catch (NoSuchFieldException | IllegalAccessException e) {
//                    throw new IllegalArgumentException("Invalid sort field: " + sortBy, e);
//                }
//            });
//
//            if ("desc".equalsIgnoreCase(sortOrder)) {
//                comparator = comparator.reversed();
//            }
//
//            contracts = contracts.stream().sorted(comparator).toList();
//        }
//
//        return contracts;
    }

    public ContractDto getContractById(Long id) {
        return this.contractRepository.findAllContractDtoByIdDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(CONTRACT_NOT_FOUND));
    }
}
