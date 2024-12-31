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
import java.util.Comparator;
import java.util.List;

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

        if (firstName != null) {
            users = users.stream()
                    .filter(user -> user.getFirstName() != null && user.getFirstName().toLowerCase().contains(firstName.toLowerCase()))
                    .toList();
        }
        if (lastName != null) {
            users = users.stream()
                    .filter(user -> user.getLastName() != null && user.getLastName().toLowerCase().contains(lastName.toLowerCase()))
                    .toList();
        }
        if (email != null) {
            users = users.stream()
                    .filter(user -> user.getEmail() != null && user.getEmail().toLowerCase().contains(email.toLowerCase()))
                    .toList();
        }
        if (username != null) {
            users = users.stream()
                    .filter(user -> user.getUsername() != null && user.getUsername().toLowerCase().contains(username.toLowerCase()))
                    .toList();
        }
        if (phone != null) {
            users = users.stream()
                    .filter(user -> user.getPhone() != null && user.getPhone().contains(phone))
                    .toList();
        }
        if (types != null && !types.isEmpty()) {
            users = users.stream()
                    .filter(user -> user.getType() != null && types.contains(user.getType()))
                    .toList();
        }
        if (shopName != null) {
            users = users.stream()
                    .filter(user -> user.getShopName() != null && user.getShopName().toLowerCase().contains(shopName.toLowerCase()))
                    .toList();
        }
        if (createdByUsername != null) {
            users = users.stream()
                    .filter(user -> user.getCreatedByUsername() != null && user.getCreatedByUsername().toLowerCase().contains(createdByUsername.toLowerCase()))
                    .toList();
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
                if (String.valueOf(newValue != null ? newValue: "").isEmpty()) {
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
