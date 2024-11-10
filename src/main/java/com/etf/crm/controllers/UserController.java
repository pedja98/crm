package com.etf.crm.controllers;

import com.etf.crm.dtos.UserDto;
import com.etf.crm.entities.User;
import com.etf.crm.exceptions.DuplicateItemException;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.etf.crm.common.CrmConstants.SuccessCodes.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.saveUser(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> partialUpdateUser(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        updates.forEach((fieldName, fieldValue) -> {
            this.userService.partialUpdateUser(id, fieldName, fieldValue);
        });
        return ResponseEntity.ok(USER_UPDATED);
    }
}
