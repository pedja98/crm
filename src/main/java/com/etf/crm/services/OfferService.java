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

import java.util.List;

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
                .opportunity(opportunity)
                .omOfferId(offerDetails.getOmOfferId())
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

    public List<OfferDto> getAllOffers() {
        return this.offerRepository.findAllOfferDtoByDeletedFalse();
    }
}
