package com.etf.crm.dtos;

import com.etf.crm.enums.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaveCustomerSessionDto {
    private String description;
    private CustomerSessionStatus status;
    private CustomerSessionType type;
    private CustomerSessionMode mode;
    private CustomerSessionOutcome outcome;
    private LocalDateTime sessionStart;
    private LocalDateTime sessionEnd;
    private Long company;
    private OpportunityType opportunityType;
}
