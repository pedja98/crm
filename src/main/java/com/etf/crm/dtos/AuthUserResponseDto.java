package com.etf.crm.dtos;

import com.etf.crm.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserResponseDto {
    private String username;
    private UserType type;
}
