package com.etf.crm.dtos;

import com.etf.crm.enums.CompanyContactRelationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyContactRelationDto {
    private Long id;
    private Long contactId;
    private String contactFullName;
    private Long companyId;
    private String companyName;
    private CompanyContactRelationType relationType;
    private String createdByUsername;
    private String modifiedByUsername;
    private Instant dateCreated;
    private Instant dateModified;
}
