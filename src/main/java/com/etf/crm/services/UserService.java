package com.etf.crm.services;

import static com.etf.crm.common.CrmConstants.ErrorCodes.*;

import com.etf.crm.config.SecurityConfig;
import com.etf.crm.dtos.UserDto;
import com.etf.crm.entities.User;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.exceptions.DuplicateItemException;
import com.etf.crm.repositories.UserRepository;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    public User getUserByUsername(String username) {
        return this.userRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new ItemNotFoundException(USER_NOT_FOUND));
    }

    public UserDto getUserById(Long id) {
        return this.userRepository.findUserDtoByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(USER_NOT_FOUND));
    }

    public List<User> getAllUsers() {
        return this.userRepository.findAllByDeletedFalse();
    }

    public User updateUser(Long id, User user) {
        User existingUser = this.userRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(USER_NOT_FOUND));
        if (!existingUser.getUsername().equals(user.getUsername()) || !existingUser.getEmail().equals(user.getEmail())) {
            this.checkDuplicateUsernameAndEmail(user.getUsername(), user.getEmail(), id);
        }
        String[] excludedFields = {"password", "createdBy", "modifiedBy", "deleted", "dateCreated", "dateM0odified"};
        for (Field field : User.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (Arrays.asList(excludedFields).contains(field.getName())) {
                    continue;
                }
                field.set(existingUser, field.get(user));
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to copy property: " + field.getName(), e);
            }
        }
        return this.userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        User user = this.userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(USER_NOT_FOUND));
        user.setDeleted(true);
        this.userRepository.save(user);
    }

    public void partialUpdateUser(Long id, String fieldName, Object fieldValue) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(USER_NOT_FOUND));
        try {
            Field field = User.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            if ("username".equals(fieldName)) {
                this.checkDuplicateUsername((String) fieldValue, id);
            } else if ("email".equals(fieldName)) {
                this.checkDuplicateEmail((String) fieldValue, id);
            } else if ("password".equals(fieldName) && fieldValue != null) {
                String encryptedPassword = stringEncryptor.encrypt((String) fieldValue);
                field.set(user, encryptedPassword);
            } else {
                field.set(user, fieldValue);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException("Invalid field name: " + fieldName, e);
        }
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

    private void checkDuplicateUsernameAndEmail(String username, String email, Long id) {
        if (this.userRepository.findByUsernameAndDeletedFalse(username)
                .filter(user -> !user.getId().equals(id)).isPresent()) {
            throw new DuplicateItemException(USERNAME_ALREADY_TAKEN);
        }
        if (this.userRepository.findByEmailAndDeletedFalse(email)
                .filter(user -> !user.getId().equals(id)).isPresent()) {
            throw new DuplicateItemException(EMAIL_ALREADY_TAKEN);
        }
    }

    private void checkDuplicateUsername(String username, Long id) {
        if (this.userRepository.findByUsernameAndDeletedFalse(username)
                .filter(user -> !user.getId().equals(id)).isPresent()) {
            throw new DuplicateItemException(USERNAME_ALREADY_TAKEN);
        }
    }

    private void checkDuplicateEmail(String email, Long id) {
        if (this.userRepository.findByEmailAndDeletedFalse(email)
                .filter(user -> !user.getId().equals(id)).isPresent()) {
            throw new DuplicateItemException(EMAIL_ALREADY_TAKEN);
        }
    }
}
