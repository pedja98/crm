package com.etf.crm.controllers;

import com.etf.crm.config.SecurityConfig;

import com.etf.crm.dtos.AuthUserRequestDto;
import com.etf.crm.dtos.AuthUserResponseDto;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.etf.crm.common.CrmConstants.ErrorCodes.WRONG_PASSWORD;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthUserRequestDto authRequest) {
        AuthUserResponseDto authUserResponse = authService.getAuthUserResponse(authRequest.getUsername());
        if (!SecurityConfig.matches(authRequest.getPassword(), authUserResponse.getPassword())) {
            throw new ItemNotFoundException(WRONG_PASSWORD);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("username", authUserResponse.getUsername());
        response.put("type", authUserResponse.getType());
        response.put("language", authUserResponse.getLanguage());

        return ResponseEntity.ok(response);
    }
}
