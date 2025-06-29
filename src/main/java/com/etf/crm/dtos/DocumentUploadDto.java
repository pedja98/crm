package com.etf.crm.dtos;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUploadDto {
    private Long contractId;
    private String fileContent;
    private String fileName;
    private String contentType;
}
