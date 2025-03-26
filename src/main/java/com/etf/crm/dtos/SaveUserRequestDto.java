package com.etf.crm.dtos;

import com.etf.crm.enums.Language;
import com.etf.crm.enums.UserType;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SaveUserRequestDto {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Language language;
    private UserType type;
}
