package com.etf.crm.services;

import com.etf.crm.enums.OfferStatus;
import com.etf.crm.filters.SetCurrentUserFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class OmOfferService {
    @Value("${offer.api.base-url}")
    private String omOfferApiBaseUrl;

    public void updateOmOfferStatus(Long offerId, OfferStatus status) {
        String url = omOfferApiBaseUrl + offerId + "/status";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("status", String.valueOf(status));

        WebClient webClient = WebClient.create();

        try {
            webClient.patch()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Username", SetCurrentUserFilter.getCurrentUser().getUsername())
                    .header("X-User-Type", String.valueOf(SetCurrentUserFilter.getCurrentUser().getType()))
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            throw e;
        }
    }

    public void closeAllOmOffersConnectedToOpportunity(Long opportunityId) {
        String url = omOfferApiBaseUrl + "/opportunity/" + opportunityId + "/offers/close";
        Map<String, String> requestBody = new HashMap<>();

        WebClient webClient = WebClient.create();

        try {
            webClient.patch()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Username", SetCurrentUserFilter.getCurrentUser().getUsername())
                    .header("X-User-Type", String.valueOf(SetCurrentUserFilter.getCurrentUser().getType()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            throw e;
        }
    }
}
