package com.etf.crm.dtos;

import com.etf.crm.enums.OfferStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OfferDto {
    private Long id;
    private String name;
    private UUID omOfferId;
    private Long companyId;
    private String companyName;
    private Long opportunityId;
    private String opportunityName;
    private Long contractId;
    private String contractName;
    private OfferStatus status;
    private String createdByUsername;
    private String modifiedByUsername;
    private Instant dateCreated;
    private Instant dateModified;
}
