package com.etf.crm.dtos;

import com.etf.crm.enums.Language;
import com.etf.crm.enums.UserType;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuthUserResponseDto {
    private String username;
    private UserType type;
    private Language language;
    private String password;
}
