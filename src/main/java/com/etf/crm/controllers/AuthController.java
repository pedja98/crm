package com.etf.crm.controllers;

import com.etf.crm.config.SecurityConfig;

import com.etf.crm.dtos.AuthUserRequestDto;
import com.etf.crm.dtos.AuthUserResponseDto;
import com.etf.crm.entities.User;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.etf.crm.common.CrmConstants.ErrorCodes.WRONG_PASSWORD;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthUserResponseDto> login(@RequestBody AuthUserRequestDto authRequest) {
        User user = userService.getUserByUsername(authRequest.getUsername());
        if (!SecurityConfig.matches(authRequest.getPassword(), user.getPassword())) {
            throw new ItemNotFoundException(WRONG_PASSWORD);
        }

        return ResponseEntity.ok((new AuthUserResponseDto(user.getUsername(), user.getType())));
    }
}
