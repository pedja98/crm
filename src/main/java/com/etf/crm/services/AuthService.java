package com.etf.crm.services;

import com.etf.crm.config.SecurityConfig;
import com.etf.crm.dtos.AuthUserDto;
import com.etf.crm.dtos.ChangePasswordRequestDto;
import com.etf.crm.entities.User;
import com.etf.crm.exceptions.BadRequestException;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.etf.crm.common.CrmConstants.ErrorCodes.*;
import static com.etf.crm.common.CrmConstants.SuccessCodes.*;
import static com.etf.crm.common.RegexConstants.*;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public AuthUserDto getAuthUserData(String username) {
        return this.userRepository.findAuthUserDtoByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new ItemNotFoundException(USER_NOT_FOUND));
    }

    public String changePassword(ChangePasswordRequestDto changePasswordData) {
        if(changePasswordData.getOldPassword() == null || changePasswordData.getNewPassword().equals(changePasswordData.getOldPassword())) {
            throw new BadRequestException(PASSWORD_NOT_CHANGED);
        }

        if (!changePasswordData.getNewPassword().matches(PASSWORD_REGEX)) {
            throw new BadRequestException(INVALID_PASSWORD_FORMAT);
        }

        User user = this.userRepository.findByUsernameAndDeletedFalse(changePasswordData.getUsername())
                .orElseThrow(() -> new ItemNotFoundException(USER_NOT_FOUND));

        if (!SecurityConfig.matches(changePasswordData.getOldPassword(), user.getPassword())) {
            throw new ItemNotFoundException(WRONG_PASSWORD);
        }

        user.setPassword(SecurityConfig.encode(changePasswordData.getNewPassword()));
        this.userRepository.save(user);

        return PASSWORD_CHANGED;
    }
}
