package com.etf.crm.controllers;

import com.etf.crm.config.SecurityConfig;

import com.etf.crm.dtos.*;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static com.etf.crm.common.CrmConstants.ErrorCodes.WRONG_PASSWORD;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthUserResponseDto> login(@RequestBody AuthUserRequestDto authRequest) {
        AuthUserDto authUserData = authService.getAuthUserData(authRequest);
        return ResponseEntity.ok((new AuthUserResponseDto(authUserData.getUsername(), authUserData.getType(), authUserData.getLanguage())));
    }

    @PatchMapping("change-password")
    public ResponseEntity<MessageResponse> changePassword(@RequestBody ChangePasswordRequestDto changePasswordData) {
        return ResponseEntity.ok(new MessageResponse(this.authService.changePassword(changePasswordData)));
    }
}
