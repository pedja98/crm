package com.etf.crm.dtos;

import com.etf.crm.enums.Language;
import com.etf.crm.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String phone;
    private UserType type;
    private Language language;
    private Long shopId;
    private String shopName;
    private String salesmen;
    private Long createdById;
    private String createdByUsername;
    private Long modifiedById;
    private String modifiedByUsername;
    private Instant dateCreated;
    private Instant dateModified;
}
