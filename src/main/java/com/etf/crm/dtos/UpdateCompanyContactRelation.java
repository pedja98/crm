package com.etf.crm.dtos;

import com.etf.crm.enums.CompanyContactRelationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompanyContactRelation {
    private String companyId;
    private CompanyContactRelationType relationType;
}
