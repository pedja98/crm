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
public class ContractReportDto {
    private Long contractId;
    private String contractName;
    private String referenceNumber;
    private LocalDate dateSigned;
    private ContractStatus contractStatus;
    private Long companyId;
    private String companyName;
    private Long opportunityId;
    private String opportunityName;
    private OpportunityType opportunityType;
    private Long shopId;
    private String shopName;
    private Long regionId;
    private String regionName;
}
