package com.etf.crm.controllers;

import com.etf.crm.dtos.AuthUserRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.AuthorizeRequestsDsl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.etf.crm.utils.*;

@RestController
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthUserRequestDto authRequest) throws Exception {
        String username = authRequest.getUsername();

        final String jwt = jwtUtil.generateToken(username);
        return ResponseEntity.ok("");
    }
}
