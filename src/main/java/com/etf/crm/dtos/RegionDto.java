package com.etf.crm.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegionDto {
    private Long id;
    private String name;
    private Long createdById;
    private String createdByUsername;
    private Long modifiedById;
    private String modifiedByUsername;
    private Instant dateCreated;
    private Instant dateModified;
}