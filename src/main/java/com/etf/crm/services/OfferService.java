package com.etf.crm.services;

import com.etf.crm.entities.Offer;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.repositories.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.etf.crm.common.CrmConstants.ErrorCodes.OFFER_NOT_FOUND;

@Service
public class OfferService {

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private CompanyService companyService;

    @Transactional
    public Offer createOffer(Long companyId, Offer offer) {
        return this.offerRepository.save(offer);
    }

    public Offer getOfferById(Long id) {
        return this.offerRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(OFFER_NOT_FOUND));
    }

    public List<Offer> getAllOffers() {
        return this.offerRepository.findAllByDeletedFalse();
    }

    @Transactional
    public void deleteOffer(Long id) {
        Offer offer = this.getOfferById(id);
        offer.setDeleted(true);
        this.offerRepository.save(offer);
    }

    @Transactional
    public Offer updateOffer(Long id, Offer offer) {
        Offer existingOffer = this.getOfferById(id);
        existingOffer.setName(offer.getName());
        existingOffer.setStatus(offer.getStatus());
        existingOffer.setModifiedBy(offer.getModifiedBy());
        return this.offerRepository.save(existingOffer);
    }
}
