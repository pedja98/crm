package com.etf.crm.dtos;

import com.etf.crm.enums.CustomerSessionMode;
import com.etf.crm.enums.CustomerSessionType;
import com.etf.crm.enums.CustomerSessionsStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSessionDto {
    private Long id;
    private String name;
    private String description;
    private CustomerSessionsStatus status;
    private CustomerSessionType type;
    private CustomerSessionMode mode;
    private LocalDateTime sessionStart;
    private LocalDateTime sessionEnd;
    private Long companyId;
    private String companyName;
    private Long opportunityId;
    private String opportunityName;
    private String createdByUsername;
    private String modifiedByUsername;
    private Instant dateCreated;
    private Instant dateModified;
}
