package com.etf.crm.services;

import com.etf.crm.dtos.ContractDto;
import com.etf.crm.dtos.ContractReportDto;
import com.etf.crm.dtos.ContractSignDto;
import com.etf.crm.dtos.CreateContractDto;
import com.etf.crm.entities.*;
import com.etf.crm.enums.*;
import com.etf.crm.exceptions.BadRequestException;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.exceptions.UnauthorizedException;
import com.etf.crm.filters.SetCurrentUserFilter;
import com.etf.crm.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.etf.crm.common.CrmConstants.SuccessCodes.*;
import static com.etf.crm.common.CrmConstants.ErrorCodes.*;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class ContractService {

    @Autowired
    private OmOfferService omOfferService;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private AuthorizationService authorizationService;

    @Value("${offer.api.base-url}")
    private String omOfferApiBaseUrl;

    public String createContract(CreateContractDto body) {
        Offer offer = this.offerRepository.findById(body.getOfferId()).orElseThrow(() -> new ItemNotFoundException(OFFER_NOT_FOUND));

        this.authorizationService.isUserAuthorizedForAction(offer.getCompany().getId());

        if (!OfferStatus.OFFER_APPROVED.equals(offer.getStatus())) {
            throw new BadRequestException(INVALID_OFFER_STATUS);
        }

        String refNo = UUID.randomUUID().toString();

        Contract contract = Contract.builder().contractObligation(0).createdBy(SetCurrentUserFilter.getCurrentUser()).deleted(false).name(offer.getName().replace("Offer", "Contract")).company(offer.getCompany()).opportunity(offer.getOpportunity()).referenceNumber(refNo).offer(offer).status(ContractStatus.CREATED).build();
        offer.setContract(this.contractRepository.save(contract));
        this.offerRepository.save(offer);
        return CREATE_CONTRACT;
    }

    public List<ContractDto> getAllContracts(String sortBy, String sortOrder, String name, String referenceNumber, List<ContractStatus> statuses, Long companyId, Long opportunityId) {
        List<ContractDto> contracts = authorizationService
                .filterByUserAccess(contractRepository.findAllContractDtoByDeletedFalse(), ContractDto::getCompanyId);

        Map<String, Object> filters = new HashMap<>();
        filters.put("name", name);
        filters.put("referenceNumber", referenceNumber);
        filters.put("status", statuses);
        filters.put("companyId", companyId);
        filters.put("opportunityId", opportunityId);

        List<Predicate<ContractDto>> predicates = filters.entrySet().stream().filter(entry -> entry.getValue() != null).map(entry -> {
            String fieldName = entry.getKey();
            Object value = entry.getValue();

            return (Predicate<ContractDto>) contract -> {
                try {
                    Field field = ContractDto.class.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Object fieldValue = field.get(contract);

                    if (value instanceof String stringValue) {
                        return fieldValue != null && fieldValue.toString().toLowerCase().contains(stringValue.toLowerCase());
                    } else if (value instanceof List<?> listValue) {
                        return fieldValue != null && listValue.contains(fieldValue);
                    } else if (value instanceof Long longValue) {
                        return fieldValue != null && fieldValue.equals(longValue);
                    }
                    return false;
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException("Invalid filter field: " + fieldName, e);
                }
            };
        }).toList();

        for (Predicate<ContractDto> predicate : predicates) {
            contracts = contracts.stream().filter(predicate).toList();
        }

        if (sortBy != null) {
            Comparator<ContractDto> comparator = Comparator.comparing(contract -> {
                try {
                    Field field = ContractDto.class.getDeclaredField(sortBy);
                    field.setAccessible(true);
                    return (Comparable) field.get(contract);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new IllegalArgumentException("Invalid sort field: " + sortBy, e);
                }
            });

            if ("desc".equalsIgnoreCase(sortOrder)) {
                comparator = comparator.reversed();
            }

            contracts = contracts.stream().sorted(comparator).toList();
        }

        return contracts;
    }

    public ContractDto getContractById(Long id) {
        ContractDto contractDto = this.contractRepository.findContractDtoByIdDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(CONTRACT_NOT_FOUND));
        this.authorizationService.isUserAuthorizedForAction(contractDto.getCompanyId());
        return contractDto;
    }

    public String signContract(Long id, ContractSignDto body) {
        Integer numberOfUploadDocs = documentRepository.countDocumentsOfContract(id);

        if (numberOfUploadDocs == 0) {
            throw new BadRequestException(DOCUMENT_NOT_UPLOADED);
        }

        if (body.getDateSigned().isEmpty()) {
            throw new BadRequestException(INVALID_REQUEST);
        }

        Contract contract = this.contractRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new ItemNotFoundException(CONTRACT_NOT_FOUND));

        this.authorizationService.isUserAuthorizedForAction(contract.getCompany().getId());

        if (this.contractRepository.existsByCompanyIdAndStatusAndDeletedFalse(contract.getCompany().getId(), ContractStatus.CONTRACT_SIGNED)) {
            throw new BadRequestException(THERE_IS_ALREADY_SIGNED_CONTRACT);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate transformedDateSigned = LocalDate.parse(body.getDateSigned(), formatter);

        contract.setDateSigned(transformedDateSigned);
        contract.setModifiedBy(SetCurrentUserFilter.getCurrentUser());
        contract.setStatus(ContractStatus.CONTRACT_SIGNED);

        this.contractRepository.save(contract);

        return CONTRACT_SIGN;
    }

    public String verifyContract(Long id) {
        Contract contract = this.contractRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new ItemNotFoundException(CONTRACT_NOT_FOUND));

        this.authorizationService.isUserAuthorizedForAction(contract.getCompany().getId());

        if (!contract.getStatus().equals(ContractStatus.CONTRACT_SIGNED)) {
            throw new BadRequestException(INVALID_REQUEST);
        }

        contract.setModifiedBy(SetCurrentUserFilter.getCurrentUser());
        contract.setStatus(ContractStatus.CONTRACT_SIGNED_AND_VERIFIED);

        this.contractRepository.save(contract);

        Offer offer = this.offerRepository.findById(contract.getOffer().getId()).orElseThrow(() -> new ItemNotFoundException(OFFER_NOT_FOUND));

        offer.setStatus(OfferStatus.CONCLUDED);
        offer.setModifiedBy(SetCurrentUserFilter.getCurrentUser());
        this.offerRepository.save(offer);

        try {
            this.omOfferService.updateOmOfferStatus(offer.getId(), OfferStatus.CONCLUDED);
        } catch (Exception e) {
            throw new RuntimeException(INVALID_REQUEST);
        }

        Opportunity opportunity = opportunityRepository.findById(contract.getOpportunity().getId()).orElseThrow(() -> new ItemNotFoundException(OPPORTUNITY_NOT_FOUND));

        opportunity.setStatus(OpportunityStatus.CLOSE_WON);
        opportunity.setModifiedBy(SetCurrentUserFilter.getCurrentUser());
        this.opportunityRepository.save(opportunity);

        Company company = this.companyRepository.findById(contract.getCompany().getId()).orElseThrow(() -> new ItemNotFoundException(COMPANY_NOT_FOUND));

        company.setStatus(opportunity.getType().equals(OpportunityType.TERMINATION) ? CompanyStatus.INACTIVE : CompanyStatus.ACTIVE);
        company.setModifiedBy(SetCurrentUserFilter.getCurrentUser());
        this.companyRepository.save(company);

        return CONTRACT_VERIFY;
    }

    public String closeContract(Long id) {
        Contract contract = this.contractRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new ItemNotFoundException(CONTRACT_NOT_FOUND));
        this.authorizationService.isUserAuthorizedForAction(contract.getCompany().getId());

        if (contract.getStatus().equals(ContractStatus.CONTRACT_SIGNED_AND_VERIFIED)) {
            throw new BadRequestException(INVALID_REQUEST);
        }

        User currentUser = SetCurrentUserFilter.getCurrentUser();
        contract.setStatus(ContractStatus.SALESMAN_CLOSED);
        contract.setModifiedBy(currentUser);
        this.contractRepository.save(contract);

        Offer offer = this.offerRepository.findById(contract.getOffer().getId()).orElseThrow(() -> new ItemNotFoundException(OFFER_NOT_FOUND));

        offer.setStatus(OfferStatus.SALESMEN_CLOSED);
        offer.setModifiedBy(currentUser);

        try {
            this.omOfferService.updateOmOfferStatus(offer.getId(), OfferStatus.SALESMEN_CLOSED);
        } catch (Exception e) {
            throw new RuntimeException(INVALID_REQUEST);
        }
        this.offerRepository.save(offer);

        return CONTRACT_CLOSED;
    }

    public List<ContractReportDto> getContractReport(List<Long> regions, List<Long> shops, LocalDateTime signatureStartDate,
                                                     LocalDateTime signatureEndDate, List<OpportunityType> opportunityTypes, List<ContractStatus> contractStatuses) {

        List<ContractReportDto> contractReportData = this.contractRepository.findAllContractReportDtoByDeletedFalse();
        User currentUser = SetCurrentUserFilter.getCurrentUser();

        if (!currentUser.getType().equals(UserType.ADMIN) && !currentUser.getType().equals(UserType.L2_MANAGER)) {
            throw new UnauthorizedException(UNAUTHORIZED);
        }

        return contractReportData.stream().filter(contract -> {
            if (regions != null && !regions.isEmpty()) {
                if (contract.getRegionId() == null || !regions.contains(contract.getRegionId())) {
                    return false;
                }
            }
            if (shops != null && !shops.isEmpty()) {
                if (contract.getShopId() == null || !shops.contains(contract.getShopId())) {
                    return false;
                }
            }
            if (signatureStartDate != null || signatureEndDate != null) {
                if (contract.getDateSigned() == null) {
                    return false;
                }

                LocalDateTime contractSignedDateTime = contract.getDateSigned().atStartOfDay();

                if (signatureStartDate != null && contractSignedDateTime.isBefore(signatureStartDate)) {
                    return false;
                }

                if (signatureEndDate != null && contractSignedDateTime.isAfter(signatureEndDate)) {
                    return false;
                }
            }
            if (opportunityTypes != null && !opportunityTypes.isEmpty()) {
                if (contract.getOpportunityType() == null || !opportunityTypes.contains(contract.getOpportunityType())) {
                    return false;
                }
            }
            if (contractStatuses != null && !contractStatuses.isEmpty()) {
                return contract.getContractStatus() != null && contractStatuses.contains(contract.getContractStatus());
            }
            return true;
        }).collect(Collectors.toList());
    }
}
