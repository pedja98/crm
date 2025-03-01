package com.etf.crm.dtos;

import com.etf.crm.enums.ContactDocumentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaveContactDto {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private ContactDocumentType documentType;
    private String documentId;
}
