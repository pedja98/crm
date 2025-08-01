package com.etf.crm.controllers;

import com.etf.crm.dtos.*;
import com.etf.crm.entities.User;
import com.etf.crm.enums.UserType;
import com.etf.crm.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<MessageResponse> createUser(@RequestBody User user) {
        return ResponseEntity.ok(new MessageResponse(userService.createUser(user)));
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
            @RequestParam(required = false, value = "type") List<UserType> types,
            @RequestParam(required = false) String shopName,
            @RequestParam(required = false) String createdByUsername) {

        List<UserDto> users = userService.getFilteredAndSortedUsers(
                sortBy, sortOrder, firstName, lastName, email, username,
                phone, types, shopName, createdByUsername
        );
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{username}")
    public ResponseEntity<MessageResponse> updateUser(@PathVariable String username, @RequestBody SaveUserRequestDto user) {
        return ResponseEntity.ok(new MessageResponse(userService.updateUser(username, user)));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable String username) {
        return ResponseEntity.ok(new MessageResponse(userService.deleteUser(username)));
    }

    @GetMapping("/assign-to")
    public ResponseEntity<List<AssignToDto>> getAllAssignTo(@RequestParam(required = false, value = "type") UserType type) {
        return ResponseEntity.ok(this.userService.getAllAssignToDto(type));
    }

    @PatchMapping("/{username}/shop")
    public ResponseEntity<MessageResponse> setUserShop(
            @PathVariable String username,
            @RequestBody SetUserShopDto dto
    ) {
        return ResponseEntity.ok(new MessageResponse(userService.setUserShop(username, dto.getShopId())));
    }
}
