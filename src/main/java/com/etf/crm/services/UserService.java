package com.etf.crm.services;

import static com.etf.crm.common.CrmConstants.ErrorCodes.USER_NOT_FOUND;

import com.etf.crm.config.SecurityConfig;
import com.etf.crm.entities.User;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.repositories.UserRepository;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StringEncryptor stringEncryptor;

    public User saveUser(User user) {
        user.setPassword(SecurityConfig.encode(user.getPassword()));
        return this.userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        return this.userRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new ItemNotFoundException(USER_NOT_FOUND));
    }

    public User getUserById(Long id) {
        return this.userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(USER_NOT_FOUND));
    }

    public List<User> getAllUsers() {
        return this.userRepository.findAllByDeletedFalse();
    }

    public User updateUser(Long id, User user) {
        Optional<User> existingUserOpt = this.userRepository.findById(id);
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setEmail(user.getEmail());
            existingUser.setUsername(user.getUsername());
            existingUser.setPhone(user.getPhone());
            existingUser.setModifiedBy(user.getModifiedBy());
            existingUser.setDeleted(user.getDeleted());
            existingUser.setPassword(SecurityConfig.encode(user.getPassword()));
            return this.userRepository.save(existingUser);
        }
        return null;
    }

    public void deleteUser(Long id) {
        Optional<User> existingUserOpt = this.userRepository.findByIdAndDeletedFalse(id);
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            existingUser.setDeleted(true);
            this.userRepository.save(existingUser);
        }
    }

    public void partialUpdateUser(Long id, String fieldName, Object fieldValue) {
        User existingUser = this.getUserById(id);
        try {
            Field field = User.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            if ("password".equals(fieldName) && fieldValue != null) {
                String encryptedPassword = stringEncryptor.encrypt((String) fieldValue);
                field.set(existingUser, encryptedPassword);
            } else {
                field.set(existingUser, fieldValue);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException("Invalid field name: " + fieldName, e);
        }
        this.userRepository.save(existingUser);
    }
}
