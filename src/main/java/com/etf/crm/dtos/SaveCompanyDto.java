package com.etf.crm.dtos;

import com.etf.crm.enums.CompanyStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaveCompanyDto {
    private String name;
    private String hqAddress;
    private String contactPhone;
    private String industry;
    private Integer numberOfEmployees;
    private Integer tin;
    private String bankName;
    private String bankAccountNumber;
    private String comment;
    private CompanyStatus status;
    private Long assignedTo;
    private Long temporaryAssignedTo;
}
