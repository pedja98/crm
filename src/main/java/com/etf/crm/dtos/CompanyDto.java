package com.etf.crm.dtos;

import com.etf.crm.enums.CompanyStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {
    private Long id;
    private String name;
    private String hqAddress;
    private String contactPhone;
    private Integer numberOfEmployees;
    private Integer tin;
    private String bankName;
    private String bankAccountNumber;
    private String industry;
    private String comment;
    private CompanyStatus status;
    private Long assignedToId;
    private String assignedToUsername;
    private Long temporaryAssignedToId;
    private String temporaryAssignedToUsername;
    private String createdByUsername;
    private String modifiedByUsername;
    private Instant dateCreated;
    private Instant dateModified;
}
