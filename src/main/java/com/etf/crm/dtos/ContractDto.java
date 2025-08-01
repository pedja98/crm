package com.etf.crm.dtos;

import com.etf.crm.enums.ContractStatus;
import com.etf.crm.enums.OpportunityType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractDto {
    private Long id;
    private String name;
    private OpportunityType opportunityType;
    private LocalDate dateSigned;
    private String referenceNumber;
    private Integer contractObligation;
    private ContractStatus status;
    private Long companyId;
    private String companyName;
    private Long opportunityId;
    private String opportunityName;
    private Long offerId;
    private String offerName;
    private String createdByUsername;
    private String modifiedByUsername;
    private Instant dateCreated;
    private Instant dateModified;
}
