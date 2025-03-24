package com.etf.crm.dtos;

import com.etf.crm.enums.CustomerSessionMode;
import com.etf.crm.enums.CustomerSessionOutcome;
import com.etf.crm.enums.CustomerSessionType;
import com.etf.crm.enums.CustomerSessionsStatus;
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
    private String name;
    private String description;
    private CustomerSessionsStatus status;
    private CustomerSessionType type;
    private CustomerSessionMode mode;
    private CustomerSessionOutcome outcome;
    private LocalDateTime sessionStart;
    private LocalDateTime sessionEnd;
    private Long company;
    private Long opportunity;
}
