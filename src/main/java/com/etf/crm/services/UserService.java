package com.etf.crm.services;

import com.etf.crm.config.SecurityConfig;
import com.etf.crm.dtos.UpdateUserRequestDto;
import com.etf.crm.dtos.UserDto;
import com.etf.crm.entities.User;
import com.etf.crm.exceptions.InvalidAttributeValueException;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.exceptions.DuplicateItemException;
import com.etf.crm.exceptions.PropertyCopyException;
import com.etf.crm.repositories.UserRepository;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.List;

import static com.etf.crm.common.CrmConstants.ErrorCodes.*;
import static com.etf.crm.common.CrmConstants.SuccessCodes.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StringEncryptor stringEncryptor;

    public User saveUser(User user) {
        this.checkDuplicateUsernameAndEmail(user.getUsername(), user.getEmail());
        user.setPassword(SecurityConfig.encode(user.getPassword()));
        return this.userRepository.save(user);
    }

    public UserDto getUserByUsername(String username) {
        return this.userRepository.findUserDtoByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new ItemNotFoundException(USER_NOT_FOUND));
    }

    public List<UserDto> getAllUsers() {
        return this.userRepository.findAllUserDtoByDeletedFalse()
                .orElseThrow(() -> new ItemNotFoundException(NO_USERS_FOUND));
    }


    @Transactional
    public String updateUser(String username, UpdateUserRequestDto userRequestData) {
        if (this.userRepository.findUserByDifferentUsernameAndSameEmail(username, userRequestData.getEmail()).isPresent()) {
            throw new DuplicateItemException(EMAIL_ALREADY_TAKEN);
        }

        User user = this.userRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new ItemNotFoundException(USER_NOT_FOUND));

        for (Field field : UpdateUserRequestDto.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object newValue = field.get(userRequestData);
                if (String.valueOf(newValue != null ? newValue: "").isEmpty()) {
                    throw new InvalidAttributeValueException(CAN_NOT_INSERT_EMPTY_VALUE);
                }

                Field userField = User.class.getDeclaredField(field.getName());
                userField.setAccessible(true);
                userField.set(user, newValue);
            } catch (NoSuchFieldException | IllegalAccessException | PropertyCopyException e) {
                throw new PropertyCopyException(ENTITY_UPDATE_ERROR);
            }
        }

        this.userRepository.save(user);
        return USER_UPDATED;
    }


    public void deleteUser(String username) {
        User user = this.userRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new ItemNotFoundException(USER_NOT_FOUND));
        user.setDeleted(true);
        this.userRepository.save(user);
    }

    private void checkDuplicateUsernameAndEmail(String username, String email) {
        if (this.userRepository.findByUsernameAndDeletedFalse(username).isPresent()) {
            throw new DuplicateItemException(USERNAME_ALREADY_TAKEN);
        }
        if (this.userRepository.findByEmailAndDeletedFalse(email).isPresent()) {
            throw new DuplicateItemException(EMAIL_ALREADY_TAKEN);
        }
    }
}
