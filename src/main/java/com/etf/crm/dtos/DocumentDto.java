package com.etf.crm.dtos;

import lombok.*;

import java.time.Instant;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDto {
    private Long id;
    private String documentName;
    private String createdByUsername;
    private String modifiedByUsername;
    private Instant dateCreated;
    private Instant dateModified;
}
