package com.etf.crm.services;

import com.etf.crm.entities.Offer;
import com.etf.crm.entities.Company;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.repositories.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;

import static com.etf.crm.common.CrmConstants.ErrorCodes.OFFER_NOT_FOUND;

@Service
public class OfferService {

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private CompanyService companyService;

    public Offer saveOffer(Long companyId, Offer offer) {
        Company company = companyService.getCompanyById(companyId);
        offer.setCompany(company);
        return this.offerRepository.save(offer);
    }

    public Offer getOfferById(Long id) {
        return this.offerRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(OFFER_NOT_FOUND));
    }

    public List<Offer> getAllOffers() {
        return this.offerRepository.findAllByDeletedFalse();
    }

    public void deleteOffer(Long id) {
        Offer offer = this.getOfferById(id);
        offer.setDeleted(true);
        this.offerRepository.save(offer);
    }

    public Offer updateOffer(Long id, Offer offerDetails) {
        Offer existingOffer = this.getOfferById(id);
        existingOffer.setName(offerDetails.getName());
        existingOffer.setStatus(offerDetails.getStatus());
        existingOffer.setModifiedBy(offerDetails.getModifiedBy());
        return this.offerRepository.save(existingOffer);
    }

    public void partialUpdateOffer(Long id, String fieldName, Object fieldValue) {
        Offer existingOffer = this.getOfferById(id);
        try {
            Field field = Offer.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(existingOffer, fieldValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException("Invalid field name: " + fieldName, e);
        }
        this.offerRepository.save(existingOffer);
    }
}
