package com.etf.crm.services;

import com.etf.crm.dtos.AuthUserResponseDto;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.etf.crm.common.CrmConstants.ErrorCodes.USER_NOT_FOUND;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public AuthUserResponseDto getAuthUserResponse(String username) {
        return this.userRepository.findAuthUserResponseDtoByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new ItemNotFoundException(USER_NOT_FOUND));
    }
}
