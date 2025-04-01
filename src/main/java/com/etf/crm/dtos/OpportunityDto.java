package com.etf.crm.dtos;

import com.etf.crm.enums.OpportunityStatus;
import com.etf.crm.enums.OpportunityType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OpportunityDto {
    private Long id;
    private String name;
    private OpportunityStatus status;
    private OpportunityType type;
    private Long companyId;
    private String companyName;
    private String createdByUsername;
    private String modifiedByUsername;
    private Instant dateCreated;
    private Instant dateModified;
}
