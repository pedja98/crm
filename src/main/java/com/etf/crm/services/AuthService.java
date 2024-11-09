package com.etf.crm.services;

import com.etf.crm.dtos.AuthUserDto;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.etf.crm.common.CrmConstants.ErrorCodes.USER_NOT_FOUND;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public AuthUserDto getAuthUserData(String username) {
        return this.userRepository.findAuthUserDtoByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new ItemNotFoundException(USER_NOT_FOUND));
    }
}
