package com.etf.crm.controllers;

import com.etf.crm.dtos.MessageResponse;
import com.etf.crm.dtos.UpdateUserRequestDto;
import com.etf.crm.dtos.UserDto;
import com.etf.crm.entities.User;
import com.etf.crm.enums.Language;
import com.etf.crm.enums.UserType;
import com.etf.crm.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) UserType type,
            @RequestParam(required = false) Language language,
            @RequestParam(required = false) String shopName,
            @RequestParam(required = false) String createdByUsername) {

        List<UserDto> users = userService.getFilteredAndSortedUsers(
                sortBy, sortOrder, firstName, lastName, email, username,
                phone, type, shopName, createdByUsername
        );
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{username}")
    public ResponseEntity<MessageResponse> updateUser(@PathVariable String username, @RequestBody UpdateUserRequestDto user) {
        return ResponseEntity.ok(new MessageResponse(userService.updateUser(username, user)));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }
}
