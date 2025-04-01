package com.etf.crm.dtos;

import com.etf.crm.enums.OpportunityType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOpportunityDto {
    private Long companyId;
    private OpportunityType type;
}
