package com.etf.crm.services;

import com.etf.crm.config.SecurityConfig;
import com.etf.crm.dtos.UpdateUserRequestDto;
import com.etf.crm.dtos.UserDto;
import com.etf.crm.entities.User;
import com.etf.crm.enums.UserType;
import com.etf.crm.exceptions.*;
import com.etf.crm.filters.SetCurrentUserFilter;
import com.etf.crm.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;

import static com.etf.crm.common.CrmConstants.ErrorCodes.*;
import static com.etf.crm.common.CrmConstants.SuccessCodes.*;
import static com.etf.crm.common.RegexConstants.PASSWORD_REGEX;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public String saveUser(User user) {
        this.checkDuplicateUsernameAndEmail(user.getUsername(), user.getEmail());
        if (!user.getPassword().matches(PASSWORD_REGEX)) {
            throw new BadRequestException(INVALID_PASSWORD_FORMAT);
        }
        user.setPassword(SecurityConfig.encode(user.getPassword()));
        User currentUser = SetCurrentUserFilter.getCurrentUser();
        user.setCreatedBy(currentUser);
        this.userRepository.save(user);

        return USER_CREATED;
    }

    public UserDto getUserByUsername(String username) {
        return this.userRepository.findUserDtoByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new ItemNotFoundException(USER_NOT_FOUND));
    }

    public User getByUsernameAndDeletedFalse(String username) {
        return this.userRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new ItemNotFoundException(USER_NOT_FOUND));
    }

    public List<UserDto> getFilteredAndSortedUsers(
            String sortBy, String sortOrder,
            String firstName, String lastName, String email, String username,
            String phone, List<UserType> types, String shopName, String createdByUsername) {

        List<UserDto> users = userRepository.findAllUserDtoByDeletedFalse()
                .orElseThrow(() -> new ItemNotFoundException(NO_USERS_FOUND));

        Map<String, Object> filters = new HashMap<>();
        filters.put("firstName", firstName);
        filters.put("lastName", lastName);
        filters.put("email", email);
        filters.put("username", username);
        filters.put("phone", phone);
        filters.put("type", types);
        filters.put("shopName", shopName);
        filters.put("createdByUsername", createdByUsername);

        List<Predicate<UserDto>> predicates = filters.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .map(entry -> {
                    String fieldName = entry.getKey();
                    Object value = entry.getValue();

                    return (Predicate<UserDto>) user -> {
                        try {
                            Field field = UserDto.class.getDeclaredField(fieldName);
                            field.setAccessible(true);
                            Object fieldValue = field.get(user);

                            if (value instanceof String stringValue) {
                                return fieldValue != null && fieldValue.toString().toLowerCase().contains(stringValue.toLowerCase());
                            } else if (value instanceof List<?> listValue) {
                                return fieldValue != null && listValue.contains(fieldValue);
                            }
                            return false;
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            throw new RuntimeException(ILLEGAL_SORT_PARAMETER + ": " + fieldName, e);
                        }
                    };
                })
                .toList();

        for (Predicate<UserDto> predicate : predicates) {
            users = users.stream().filter(predicate).toList();
        }

        if (sortBy != null) {
            Comparator<UserDto> comparator = Comparator.comparing(user -> {
                try {
                    Field field = UserDto.class.getDeclaredField(sortBy);
                    field.setAccessible(true);
                    return (Comparable) field.get(user);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new IllegalArgumentException(ILLEGAL_SORT_PARAMETER + ": " + sortBy, e);
                }
            });

            if ("desc".equalsIgnoreCase(sortOrder)) {
                comparator = comparator.reversed();
            }
            users = users.stream().sorted(comparator).toList();
        }

        return users;
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
                if (String.valueOf(newValue != null ? newValue : "").isEmpty()) {
                    System.out.println("J");
                    throw new InvalidAttributeValueException(CAN_NOT_INSERT_EMPTY_VALUE);
                }

                Field userField = User.class.getDeclaredField(field.getName());
                userField.setAccessible(true);
                userField.set(user, newValue);
            } catch (NoSuchFieldException | IllegalAccessException | PropertyCopyException e) {
                throw new PropertyCopyException(ENTITY_UPDATE_ERROR);
            }
        }

        User currentUser = SetCurrentUserFilter.getCurrentUser();
        user.setModifiedBy(currentUser);

        this.userRepository.save(user);
        return USER_UPDATED;
    }

    @Transactional
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
